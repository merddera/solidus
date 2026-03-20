package com.example.solidus.domain.repository

import com.example.solidus.domain.model.CurrencyRate
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    fun getRates(): Flow<List<CurrencyRate>>
    suspend fun syncRates()
}
