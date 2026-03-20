package com.example.solidus.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
fun HomeTab(
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: TransactionViewModel = koinViewModel()
) {
    val balance by viewModel.balance.collectAsState()
    val hideBalance by viewModel.hideBalance.collectAsState()
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val transactionsState by viewModel.convertedTransactions.collectAsState()
    val categories by viewModel.categories.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Rounded Modern Balance Card
        Card(
            modifier = Modifier.fillMaxWidth().height(160.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                Text("Общий баланс", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleMedium)
                val formattedBalance = java.lang.String.format(java.util.Locale.US, "%.2f %s", balance, selectedCurrency)
                Text(
                    text = if (hideBalance) "****" else formattedBalance,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.headlineLarge
                )
                
                Button(
                    onClick = onNavigateToAdd,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary, 
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Добавить расход")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Последние транзакции", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            when (val state = transactionsState) {
                is com.example.solidus.presentation.UiState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp))
                is com.example.solidus.presentation.UiState.Error -> Text("Ошибка: ${state.message}", color = MaterialTheme.colorScheme.error)
                is com.example.solidus.presentation.UiState.Success -> {
                    if (state.data.isEmpty()) {
                        Text("Нет транзакций", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 32.dp))
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.data.take(5)) { transaction ->
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

@Composable
fun TransactionItem(transaction: Transaction, category: Category?, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
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
                Text(
                    text = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(transaction.date)),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Text(
                text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}${java.lang.String.format(java.util.Locale.US, "%.2f", transaction.amount)} ${transaction.currencyCode}",
                color = if (transaction.type == TransactionType.INCOME) Color(0xFF4CAF50) else Color(0xFFE53935),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
