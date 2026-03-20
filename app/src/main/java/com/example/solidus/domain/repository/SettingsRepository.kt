package com.example.solidus.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val selectedCurrency: Flow<String>
    val hideBalance: Flow<Boolean>
    val lastCurrencyUpdate: Flow<Long>

    suspend fun setSelectedCurrency(currencyCode: String)
    suspend fun setHideBalance(hide: Boolean)
    suspend fun setLastCurrencyUpdate(timestamp: Long)
}
