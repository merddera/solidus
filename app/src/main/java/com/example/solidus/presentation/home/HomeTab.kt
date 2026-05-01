package com.example.solidus.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import com.example.solidus.presentation.utils.getIconByName
import com.example.solidus.ui.theme.GreenPrimary
import org.koin.androidx.compose.koinViewModel
import com.example.solidus.domain.model.Transaction
import com.example.solidus.domain.model.TransactionType
import com.example.solidus.domain.model.Category
import com.example.solidus.presentation.TransactionViewModel
import com.example.solidus.presentation.UiState
import com.example.solidus.R
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTab(
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: TransactionViewModel = koinViewModel()
) {
    val balance by viewModel.balance.collectAsStateWithLifecycle()
    val hideBalance by viewModel.hideBalance.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val transactionsState by viewModel.convertedTransactions.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

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
                Text(stringResource(R.string.total_balance), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleMedium)
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
                    Icon(Icons.Default.Add, contentDescription = "add")
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.add))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Text(stringResource(R.string.recent_transactions), style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            when (val state = transactionsState) {
                is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp))
                is UiState.Error -> Text(stringResource(R.string.error, state.message), color = MaterialTheme.colorScheme.error)
                is UiState.Success<List<Transaction>> -> {
                    val txs = state.data
                    if (txs.isEmpty()) {
                        Text(stringResource(R.string.no_transactions), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 32.dp))
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(items = txs.take(5), key = { it.id }) { transaction ->
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
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (category != null) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(android.graphics.Color.parseColor(category.color)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getIconByName(category.iconName),
                            contentDescription = category.name,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column {
                    Text(text = transaction.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${transaction.type.name} ${if (category != null) "- ${category.name}" else ""}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = dateFormatter.format(java.util.Date(transaction.date)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            val isIncome = transaction.type == TransactionType.INCOME
            val amountPrefix = if (isIncome) "+" else "-"
            val amountColor = if (isIncome) GreenPrimary else MaterialTheme.colorScheme.error
            Text(
                text = "$amountPrefix${String.format(Locale.US, "%.2f", transaction.amount)} ${transaction.currencyCode}",
                color = amountColor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
