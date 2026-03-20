package com.example.solidus.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val selectedCurrency by viewModel.selectedCurrency.collectAsState()
    val hideBalance by viewModel.hideBalance.collectAsState()
    val currencies by viewModel.availableCurrencies.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Скрыть баланс на главном экране", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = hideBalance,
                    onCheckedChange = { viewModel.setHideBalance(it) }
                )
            }

            Divider()

            Text("Базовая валюта", style = MaterialTheme.typography.bodyLarge)
            
            val popularCurrencies = mapOf("USD" to "$", "EUR" to "€", "RUB" to "₽", "CNY" to "¥", "GBP" to "£")

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = popularCurrencies[selectedCurrency]?.let { "$it $selectedCurrency" } ?: selectedCurrency,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Валюта") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    val uniqueCodes = currencies.map { it.currencyCode }.distinct().sorted()
                    
                    popularCurrencies.forEach { (code, symbol) ->
                        DropdownMenuItem(
                            text = { Text("$symbol $code") },
                            onClick = {
                                viewModel.setSelectedCurrency(code)
                                expanded = false
                            }
                        )
                    }
                    
                    Divider()
                    
                    uniqueCodes.filterNot { popularCurrencies.containsKey(it) }.forEach { code ->
                        DropdownMenuItem(
                            text = { Text(code) },
                            onClick = {
                                viewModel.setSelectedCurrency(code)
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { showClearDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Очистить все данные")
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Внимание") },
            text = { Text("Вы уверены, что хотите безвозвратно удалить все транзакции? Это действие нельзя отменить.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllTransactions()
                    showClearDialog = false
                }) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Отмена") }
            }
        )
    }
}
