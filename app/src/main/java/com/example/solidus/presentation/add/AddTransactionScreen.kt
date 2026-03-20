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
import org.koin.androidx.compose.koinViewModel
import com.example.solidus.domain.model.TransactionType
import com.example.solidus.presentation.TransactionViewModel

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
    val activeCategories by viewModel.activeCategories.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<com.example.solidus.domain.model.Category?>(null) }
    var titleError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    var selectedTimeMillis by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(transactionId) {
        if (transactionId != null) {
            val transaction = viewModel.getTransaction(transactionId)
            if (transaction != null) {
                title = transaction.title
                amount = transaction.amount.toString()
                isIncome = transaction.type == TransactionType.INCOME
                selectedCategory = activeCategories.find { it.id == transaction.categoryId }
                datePickerState.setSelection(transaction.date)
                selectedTimeMillis = transaction.date
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("ОК") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Новая категория") },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    label = { Text("Название") }
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
                    Text("Добавить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить транзакцию") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                label = { Text("Название") },
                isError = titleError,
                supportingText = { if (titleError) Text("Обязательное поле") },
                keyboardOptions = KeyboardOptions(capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it; amountError = false },
                label = { Text("Сумма") },
                isError = amountError,
                supportingText = { if (amountError) Text("Сумма должна быть > 0") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "Выберите категорию",
                    onValueChange = {},
                    readOnly = true,
                    isError = categoryError,
                    supportingText = { if (categoryError) Text("Выберите категорию") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    activeCategories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
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
                        text = { Text("➕ Добавить категорию...", color = MaterialTheme.colorScheme.primary) },
                        onClick = {
                            expanded = false
                            showAddCategoryDialog = true
                        }
                    )
                }
            }

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("Доход?")
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
                    value = datePickerState.selectedDateMillis?.let { 
                        java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault()).format(java.util.Date(it)) 
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Дата") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Выбрать дату")
                        }
                    }
                )

                OutlinedTextField(
                    value = selectedTimeMillis?.let { 
                        java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(it)) 
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Время") },
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
                            Icon(Icons.Default.Add, contentDescription = "Выбрать время")
                        }
                    }
                )
            }

            Button(
                onClick = {
                    val result = viewModel.validateInput(title, amount, selectedCategory?.id)
                    titleError = !result.isTitleValid
                    amountError = !result.isAmountValid
                    categoryError = !result.isCategoryValid

                    if (result.isValid) {
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
                                date = combinedDate
                            )
                        } else {
                            viewModel.addTransaction(
                                title = title,
                                amount = amount.toDouble(),
                                type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE,
                                categoryId = selectedCategory?.id,
                                date = combinedDate
                            )
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }
        }
    }
}
