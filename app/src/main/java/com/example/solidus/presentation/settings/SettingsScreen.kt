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

            HorizontalDivider()

            Text("Базовая валюта", style = MaterialTheme.typography.bodyLarge)
            
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedCurrency,
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
                    if (uniqueCodes.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text(selectedCurrency) },
                            onClick = { expanded = false }
                        )
                    } else {
                        uniqueCodes.forEach { code ->
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
            }
        }
    }
}
