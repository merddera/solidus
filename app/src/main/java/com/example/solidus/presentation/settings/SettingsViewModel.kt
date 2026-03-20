package com.example.solidus.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.solidus.domain.model.CurrencyRate
import com.example.solidus.domain.repository.CurrencyRepository
import com.example.solidus.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    currencyRepository: CurrencyRepository
) : ViewModel() {

    val selectedCurrency: StateFlow<String> = settingsRepository.selectedCurrency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "RUB")

    val hideBalance: StateFlow<Boolean> = settingsRepository.hideBalance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val availableCurrencies: StateFlow<List<CurrencyRate>> = currencyRepository.getRates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedCurrency(currencyCode: String) {
        viewModelScope.launch {
            settingsRepository.setSelectedCurrency(currencyCode)
        }
    }

    fun setHideBalance(hide: Boolean) {
        viewModelScope.launch {
            settingsRepository.setHideBalance(hide)
        }
    }
}
