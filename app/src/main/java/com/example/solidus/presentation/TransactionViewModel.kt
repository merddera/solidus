package com.example.solidus.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solidus.domain.model.Transaction
import com.example.solidus.domain.model.TransactionType
import com.example.solidus.domain.usecase.AddTransactionUseCase
import com.example.solidus.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    getTransactionsUseCase: GetTransactionsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {

    val transactions: StateFlow<List<Transaction>> = getTransactionsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTransaction(title: String, amount: Double, type: TransactionType) {
        viewModelScope.launch {
            try {
                addTransactionUseCase(
                    Transaction(
                        title = title,
                        amount = amount,
                        date = System.currentTimeMillis(),
                        type = type
                    )
                )
            } catch (e: Exception) {
                // Here we would handle UI events for errors
                e.printStackTrace()
            }
        }
    }
}
