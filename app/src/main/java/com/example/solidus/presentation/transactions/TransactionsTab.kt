package com.example.solidus.presentation.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.example.solidus.presentation.TransactionViewModel
import com.example.solidus.presentation.UiState
import com.example.solidus.presentation.home.TransactionItem
import com.example.solidus.R
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsTab(
    onNavigateToEdit: (Long) -> Unit,
    viewModel: TransactionViewModel = koinViewModel()
) {
    val transactionsState by viewModel.convertedTransactions.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    var showDateRangePicker by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()
    val dateFormatter = remember { SimpleDateFormat("dd.MM", Locale.getDefault()) }

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
                }) { Text(stringResource(R.string.apply)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDateRangePicker = false
                    dateRangePickerState.setSelection(null, null)
                    viewModel.setDateFilter(null, null)
                }) { Text(stringResource(R.string.reset)) }
            }
        ) {
            DateRangePicker(state = dateRangePickerState, modifier = Modifier.fillMaxWidth().height(400.dp))
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        val selectedCategoryId by viewModel.selectedCategoryId.collectAsStateWithLifecycle()

        androidx.compose.foundation.lazy.LazyRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { FilterChip(selected = selectedCategoryId == null, onClick = { viewModel.setCategoryFilter(null) }, label = { Text(stringResource(R.string.filter_all)) }) }
            item {
                val hasDateFilter = dateRangePickerState.selectedStartDateMillis != null
                FilterChip(
                    selected = hasDateFilter,
                    onClick = { showDateRangePicker = true },
                    label = {
                        if (hasDateFilter) {
                            val start = dateFormatter.format(java.util.Date(dateRangePickerState.selectedStartDateMillis!!))
                            val end = dateRangePickerState.selectedEndDateMillis?.let { dateFormatter.format(java.util.Date(it)) } ?: ""
                            Text("$start - $end")
                        } else {
                            Text(stringResource(R.string.filter_period))
                        }
                    }
                )
            }
            items(items = categories, key = { it.id }) { category ->
                FilterChip(selected = selectedCategoryId == category.id, onClick = { viewModel.setCategoryFilter(category.id) }, label = { Text(category.name) })
            }
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            when (val state = transactionsState) {
                is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp))
                is UiState.Error -> Text(stringResource(R.string.error, state.message), color = MaterialTheme.colorScheme.error)
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        Text(stringResource(R.string.no_transactions), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 32.dp))
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 80.dp)) {
                            items(items = state.data, key = { it.id }) { transaction ->
                                val category = categories.find { it.id == transaction.categoryId }
                                TransactionItem(transaction, category) {
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


