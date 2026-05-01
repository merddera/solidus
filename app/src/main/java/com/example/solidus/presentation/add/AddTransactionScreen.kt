package com.example.solidus.presentation.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.solidus.R
import org.koin.androidx.compose.koinViewModel
import com.example.solidus.domain.model.TransactionType
import com.example.solidus.presentation.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    transactionId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: TransactionViewModel = koinViewModel()
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }
    val activeCategories by viewModel.activeCategories.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedAccount by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<com.example.solidus.domain.model.Category?>(null) }
    var selectedAccount by remember { mutableStateOf<com.example.solidus.domain.model.Account?>(null) }
    var titleError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }
    var accountError by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    var selectedTimeMillis by remember { mutableStateOf<Long?>(null) }
    val dateDisplayFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }
    val timeDisplayFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    LaunchedEffect(transactionId, accounts) {
        if (accounts.isNotEmpty() && selectedAccount == null) {
            selectedAccount = accounts.first()
        }
        if (transactionId != null) {
            val transaction = viewModel.getTransaction(transactionId)
            if (transaction != null) {
                title = transaction.title
                amount = transaction.amount.toString()
                isIncome = transaction.type == TransactionType.INCOME
                selectedCategory = activeCategories.find { it.id == transaction.categoryId }
                selectedAccount = accounts.find { it.id == transaction.accountId } ?: accounts.firstOrNull()
                datePickerState.setSelection(transaction.date)
                selectedTimeMillis = transaction.date
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.cancel)) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text(stringResource(R.string.new_category)) },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text(stringResource(R.string.title)) }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newCategoryName.isNotBlank()) {
                        viewModel.addCategory(newCategoryName)
                        showAddCategoryDialog = false
                        newCategoryName = ""
                    }
                }) {
                    Text(stringResource(R.string.add))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_transaction)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it; titleError = false },
                label = { Text(stringResource(R.string.title)) },
                isError = titleError,
                supportingText = { if (titleError) Text(stringResource(R.string.required_field)) },
                keyboardOptions = KeyboardOptions(capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it; amountError = false },
                label = { Text(stringResource(R.string.amount)) },
                isError = amountError,
                supportingText = { if (amountError) Text(stringResource(R.string.amount_positive)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expandedAccount,
                onExpandedChange = { expandedAccount = !expandedAccount }
            ) {
                OutlinedTextField(
                    value = selectedAccount?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    isError = accountError,
                    label = { Text(stringResource(R.string.account)) },
                    supportingText = { if (accountError) Text(stringResource(R.string.required_field)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAccount) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedAccount,
                    onDismissRequest = { expandedAccount = false }
                ) {
                    accounts.forEach { accountItem ->
                        DropdownMenuItem(
                            text = { Text(accountItem.name) },
                            onClick = {
                                selectedAccount = accountItem
                                expandedAccount = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = !expandedCategory }
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    isError = categoryError,
                    label = { Text(stringResource(R.string.category)) },
                    supportingText = { if (categoryError) Text(stringResource(R.string.required_field)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    activeCategories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category
                                expandedCategory = false
                            },
                            trailingIcon = {
                                IconButton(onClick = { 
                                    viewModel.archiveCategory(category.id)
                                    if (selectedCategory?.id == category.id) selectedCategory = null 
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Category")
                                }
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("➕ " + stringResource(R.string.new_category), color = MaterialTheme.colorScheme.primary) },
                        onClick = {
                            expandedCategory = false
                            showAddCategoryDialog = true
                        }
                    )
                }
            }

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(stringResource(R.string.is_income))
                Switch(
                    checked = isIncome,
                    onCheckedChange = { isIncome = it },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = datePickerState.selectedDateMillis?.let { dateDisplayFormatter.format(java.util.Date(it)) } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.date)) },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.pick_date))
                        }
                    }
                )

                OutlinedTextField(
                    value = selectedTimeMillis?.let { timeDisplayFormatter.format(java.util.Date(it)) } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.time)) },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        val context = androidx.compose.ui.platform.LocalContext.current
                        IconButton(onClick = { 
                            val calendar = java.util.Calendar.getInstance()
                            if (selectedTimeMillis != null) calendar.timeInMillis = selectedTimeMillis!!
                            android.app.TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    val newCal = java.util.Calendar.getInstance()
                                    newCal.set(java.util.Calendar.HOUR_OF_DAY, hour)
                                    newCal.set(java.util.Calendar.MINUTE, minute)
                                    selectedTimeMillis = newCal.timeInMillis
                                },
                                calendar.get(java.util.Calendar.HOUR_OF_DAY),
                                calendar.get(java.util.Calendar.MINUTE),
                                true
                            ).show()
                        }) {
                            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.pick_time))
                        }
                    }
                )
            }

            Button(
                onClick = {
                    val result = viewModel.validateInput(title, amount, selectedCategory?.id, selectedAccount?.id)
                    titleError = !result.isTitleValid
                    amountError = !result.isAmountValid
                    categoryError = !result.isCategoryValid
                    accountError = !result.isAccountValid

                    if (result.isValid && selectedAccount != null) {
                        val finalDate = datePickerState.selectedDateMillis
                        val combinedDate = if (selectedTimeMillis != null) {
                            val dateCal = java.util.Calendar.getInstance().apply { timeInMillis = finalDate ?: System.currentTimeMillis() }
                            val timeCal = java.util.Calendar.getInstance().apply { timeInMillis = selectedTimeMillis!! }
                            dateCal.set(java.util.Calendar.HOUR_OF_DAY, timeCal.get(java.util.Calendar.HOUR_OF_DAY))
                            dateCal.set(java.util.Calendar.MINUTE, timeCal.get(java.util.Calendar.MINUTE))
                            dateCal.timeInMillis
                        } else if (finalDate != null) {
                            val dateCal = java.util.Calendar.getInstance().apply { timeInMillis = finalDate }
                            val now = java.util.Calendar.getInstance()
                            dateCal.set(java.util.Calendar.HOUR_OF_DAY, now.get(java.util.Calendar.HOUR_OF_DAY))
                            dateCal.set(java.util.Calendar.MINUTE, now.get(java.util.Calendar.MINUTE))
                            dateCal.timeInMillis
                        } else {
                            System.currentTimeMillis()
                        }

                        if (transactionId != null) {
                            viewModel.updateTransaction(
                                id = transactionId,
                                title = title,
                                amount = amount.toDouble(),
                                type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE,
                                categoryId = selectedCategory?.id,
                                accountId = selectedAccount!!.id,
                                date = combinedDate
                            )
                        } else {
                            viewModel.addTransaction(
                                title = title,
                                amount = amount.toDouble(),
                                type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE,
                                categoryId = selectedCategory?.id,
                                accountId = selectedAccount!!.id,
                                date = combinedDate
                            )
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}
