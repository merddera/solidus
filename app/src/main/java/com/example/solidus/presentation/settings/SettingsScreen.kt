package com.example.solidus.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.solidus.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val hideBalance by viewModel.hideBalance.collectAsStateWithLifecycle()
    val currencies by viewModel.availableCurrencies.collectAsStateWithLifecycle()
    var showClearDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.hide_balance), style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = hideBalance,
                    onCheckedChange = { viewModel.setHideBalance(it) }
                )
            }

            Divider()

            Text(stringResource(R.string.base_currency), style = MaterialTheme.typography.bodyLarge)
            
            val popularCurrencies = mapOf("USD" to "$", "EUR" to "€", "RUB" to "₽", "CNY" to "¥", "GBP" to "£")

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = popularCurrencies[selectedCurrency]?.let { "$it $selectedCurrency" } ?: selectedCurrency,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.currency)) },
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
                Text(stringResource(R.string.clear_data))
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(R.string.warning)) },
            text = { Text(stringResource(R.string.clear_data_message)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllTransactions()
                    showClearDialog = false
                }) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}
