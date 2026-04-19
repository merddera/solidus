package com.example.solidus.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solidus.domain.model.Transaction
import com.example.solidus.domain.model.TransactionType
import com.example.solidus.domain.usecase.AddTransactionUseCase
import com.example.solidus.domain.usecase.GetTransactionsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.example.solidus.presentation.UiState

import com.example.solidus.domain.model.Category
import com.example.solidus.domain.model.Account
import com.example.solidus.domain.repository.AccountRepository

class TransactionViewModel(
    getTransactionsUseCase: GetTransactionsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getTransactionByIdUseCase: com.example.solidus.domain.usecase.GetTransactionByIdUseCase,
    private val updateTransactionUseCase: com.example.solidus.domain.usecase.UpdateTransactionUseCase,
    getCategoriesUseCase: com.example.solidus.domain.usecase.GetCategoriesUseCase,
    private val addCategoryUseCase: com.example.solidus.domain.usecase.AddCategoryUseCase,
    private val archiveCategoryUseCase: com.example.solidus.domain.usecase.ArchiveCategoryUseCase,
    private val currencyRepository: com.example.solidus.domain.repository.CurrencyRepository,
    private val settingsRepository: com.example.solidus.domain.repository.SettingsRepository,
    private val accountRepository: AccountRepository,
    private val currencyConverter: com.example.solidus.domain.usecase.CurrencyConverterUseCase
) : ViewModel() {

    init {
        viewModelScope.launch {
            currencyRepository.syncRates()
        }
    }

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    private val _startDate = MutableStateFlow<Long?>(null)
    val startDate: StateFlow<Long?> = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow<Long?>(null)
    val endDate: StateFlow<Long?> = _endDate.asStateFlow()

    val selectedCurrency = settingsRepository.selectedCurrency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "RUB")

    val hideBalance = settingsRepository.hideBalance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val rates = currencyRepository.getRates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setCategoryFilter(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
    }

    fun setDateFilter(start: Long?, end: Long?) {
        _startDate.value = start
        _endDate.value = end
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val transactions: StateFlow<UiState<List<Transaction>>> = combine(
        _selectedCategoryId, _startDate, _endDate
    ) { categoryId, start, end ->
        Triple(categoryId, start, end)
    }.flatMapLatest { (categoryId, start, end) ->
        getTransactionsUseCase(categoryId, start, end)
            .map { UiState.Success(it) as UiState<List<Transaction>> }
            .onStart { emit(UiState.Loading) }
            .catch { emit(UiState.Error(it.message ?: "Unknown error")) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading
    )

    val convertedTransactions: StateFlow<UiState<List<Transaction>>> = combine(
        transactions, selectedCurrency, rates
    ) { state, targetCurrency, rateList ->
        if (state is UiState.Success) {
            val converted = state.data.map { tx ->
                val newAmount = currencyConverter.convert(tx.amount, tx.currencyCode, targetCurrency, rateList)
                tx.copy(amount = newAmount, currencyCode = targetCurrency)
            }
            UiState.Success(converted)
        } else {
            state
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    val categories: StateFlow<List<Category>> = getCategoriesUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activeCategories: StateFlow<List<Category>> = categories
        .map { list -> list.filter { !it.isArchived } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val accounts: StateFlow<List<Account>> = accountRepository.getAccounts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val balance: StateFlow<Double> = convertedTransactions
        .map { state ->
            if (state is UiState.Success) {
                state.data.sumOf { if (it.type == TransactionType.INCOME) it.amount else -it.amount }
            } else {
                0.0
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    val categoryExpenses: StateFlow<List<CategoryExpense>> = combine(
        convertedTransactions, categories
    ) { state, catList ->
        if (state is UiState.Success) {
            val expenses = state.data.filter { it.type == TransactionType.EXPENSE }
            val totalExpense = expenses.sumOf { it.amount }
            if (totalExpense == 0.0) return@combine emptyList()

            val grouped = expenses.groupBy { it.categoryId }
            grouped.map { (catId, txs) ->
                val catAmount = txs.sumOf { it.amount }
                val cat = catList.find { it.id == catId }
                CategoryExpense(
                    category = cat,
                    amount = catAmount,
                    percentage = (catAmount / totalExpense).toFloat()
                )
            }.sortedByDescending { it.amount }
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun validateInput(title: String, amountText: String, categoryId: Long?, accountId: Long?): ValidationResult {
        val titleValid = title.isNotBlank()
        val amount = amountText.toDoubleOrNull()
        val amountValid = amount != null && amount > 0
        val categoryValid = categoryId != null
        val accountValid = accountId != null
        return ValidationResult(titleValid, amountValid, categoryValid, accountValid)
    }

    fun archiveCategory(categoryId: Long) {
        viewModelScope.launch {
            try {
                archiveCategoryUseCase(categoryId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addCategory(name: String, color: String = "#000000") {
        viewModelScope.launch {
            try {
                addCategoryUseCase(name, color)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addTransaction(title: String, amount: Double, type: TransactionType, categoryId: Long? = null, accountId: Long, date: Long = System.currentTimeMillis()) {
        viewModelScope.launch {
            try {
                addTransactionUseCase(
                    Transaction(
                        title = title,
                        amount = amount,
                        date = date,
                        type = type,
                        categoryId = categoryId,
                        accountId = accountId,
                        currencyCode = selectedCurrency.value
                    )
                )
            } catch (e: Exception) {
                // Here we would handle UI events for errors
                e.printStackTrace()
            }
        }
    }

    suspend fun getTransaction(id: Long): Transaction? = getTransactionByIdUseCase(id)

    fun updateTransaction(id: Long, title: String, amount: Double, type: TransactionType, categoryId: Long? = null, accountId: Long, date: Long) {
        viewModelScope.launch {
            try {
                updateTransactionUseCase(
                    Transaction(
                        id = id,
                        title = title,
                        amount = amount,
                        date = date,
                        type = type,
                        categoryId = categoryId,
                        accountId = accountId,
                        currencyCode = selectedCurrency.value
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

data class ValidationResult(
    val isTitleValid: Boolean,
    val isAmountValid: Boolean,
    val isCategoryValid: Boolean,
    val isAccountValid: Boolean
) {
    val isValid: Boolean get() = isTitleValid && isAmountValid && isCategoryValid && isAccountValid
}

data class CategoryExpense(
    val category: Category?,
    val amount: Double,
    val percentage: Float
)
