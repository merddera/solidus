package com.example.solidus.presentation.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.example.solidus.presentation.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsTab(
    onNavigateToEdit: (Long) -> Unit,
    viewModel: TransactionViewModel = koinViewModel()
) {
    val transactionsState by viewModel.convertedTransactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var showDateRangePicker by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()

    if (showDateRangePicker) {
        DatePickerDialog(
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                TextButton(onClick = { 
                    showDateRangePicker = false
                    viewModel.setDateFilter(
                        dateRangePickerState.selectedStartDateMillis,
                        dateRangePickerState.selectedEndDateMillis
                    )
                }) { Text("Применить") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDateRangePicker = false
                    dateRangePickerState.setSelection(null, null)
                    viewModel.setDateFilter(null, null)
                }) { Text("Сбросить") }
            }
        ) {
            DateRangePicker(state = dateRangePickerState, modifier = Modifier.fillMaxWidth().height(400.dp))
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()

        androidx.compose.foundation.lazy.LazyRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { FilterChip(selected = selectedCategoryId == null, onClick = { viewModel.setCategoryFilter(null) }, label = { Text("Все") }) }
            item {
                val hasDateFilter = dateRangePickerState.selectedStartDateMillis != null
                FilterChip(
                    selected = hasDateFilter,
                    onClick = { showDateRangePicker = true },
                    label = {
                        if (hasDateFilter) {
                            val sdf = java.text.SimpleDateFormat("dd.MM", java.util.Locale.getDefault())
                            val start = sdf.format(java.util.Date(dateRangePickerState.selectedStartDateMillis!!))
                            val end = dateRangePickerState.selectedEndDateMillis?.let { sdf.format(java.util.Date(it)) } ?: ""
                            Text("$start - $end")
                        } else {
                            Text("Период")
                        }
                    }
                )
            }
            items(categories) { category ->
                FilterChip(selected = selectedCategoryId == category.id, onClick = { viewModel.setCategoryFilter(category.id) }, label = { Text(category.name) })
            }
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            when (val state = transactionsState) {
                is com.example.solidus.presentation.UiState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp))
                is com.example.solidus.presentation.UiState.Error -> Text("Ошибка: ${state.message}", color = MaterialTheme.colorScheme.error)
                is com.example.solidus.presentation.UiState.Success -> {
                    if (state.data.isEmpty()) {
                        Text("Нет транзакций", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 32.dp))
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 80.dp)) {
                            items(state.data) { transaction ->
                                val category = categories.find { it.id == transaction.categoryId }
                                com.example.solidus.presentation.home.TransactionItem(transaction, category) {
                                    onNavigateToEdit(transaction.id)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
