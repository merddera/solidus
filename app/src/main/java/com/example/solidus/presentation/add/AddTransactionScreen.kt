package com.example.solidus.presentation.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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

            Button(
                onClick = {
                    val result = viewModel.validateInput(title, amount, selectedCategory?.id)
                    titleError = !result.isTitleValid
                    amountError = !result.isAmountValid
                    categoryError = !result.isCategoryValid

                    if (result.isValid) {
                        viewModel.addTransaction(
                            title = title,
                            amount = amount.toDouble(),
                            type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE,
                            categoryId = selectedCategory?.id
                        )
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
