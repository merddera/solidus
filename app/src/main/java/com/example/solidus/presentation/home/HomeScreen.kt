package com.example.solidus.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.example.solidus.domain.model.Transaction
import com.example.solidus.domain.model.TransactionType
import com.example.solidus.domain.model.Category
import com.example.solidus.presentation.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAdd: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: TransactionViewModel = koinViewModel()
) {
    val transactionsState by viewModel.transactions.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val categories by viewModel.categories.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solidus Balance: $balance") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(androidx.compose.material.icons.Icons.Default.Settings, contentDescription = "Настройки")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()

            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoryId == null,
                        onClick = { viewModel.setCategoryFilter(null) },
                        label = { Text("Все") }
                    )
                }
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategoryId == category.id,
                        onClick = { viewModel.setCategoryFilter(category.id) },
                        label = { Text(category.name) }
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                when (val state = transactionsState) {
                is com.example.solidus.presentation.UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp))
                }
                is com.example.solidus.presentation.UiState.Error -> {
                    Text(
                        text = "Ошибка: ${state.message}", 
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 32.dp)
                    )
                }
                is com.example.solidus.presentation.UiState.Success -> {
                    if (state.data.isEmpty()) {
                        Text(
                            text = "Нет транзакций", 
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 32.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp)
                        ) {
                            items(state.data) { transaction ->
                                val category = categories.find { it.id == transaction.categoryId }
                                TransactionItem(transaction, category)
                            }
                        }
                    }
                }
            }
        }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, category: Category?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = transaction.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${transaction.type.name} ${if (category != null) "- ${category.name}" else ""}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}${transaction.amount}",
                color = if (transaction.type == TransactionType.INCOME) Color(0xFF4CAF50) else Color(0xFFE53935),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
