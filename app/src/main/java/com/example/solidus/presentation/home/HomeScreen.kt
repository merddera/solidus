package com.example.solidus.presentation.home

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.solidus.domain.model.Transaction
import com.example.solidus.domain.model.TransactionType
import com.example.solidus.presentation.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAdd: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    
    // Balance Calculation (Phase 8)
    val balance = transactions.sumOf { 
        if (it.type == TransactionType.INCOME) it.amount else -it.amount 
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solidus Balance: $balance") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(transactions) { transaction ->
                TransactionItem(transaction)
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
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
                Text(text = transaction.type.name, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}${transaction.amount}",
                color = if (transaction.type == TransactionType.INCOME) Color(0xFF4CAF50) else Color(0xFFE53935),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
