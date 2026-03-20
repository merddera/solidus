package com.example.solidus.data.repository

import com.example.solidus.data.local.dao.CurrencyRateDao
import com.example.solidus.data.local.entity.CurrencyRateEntity
import com.example.solidus.data.remote.api.CurrencyApiService
import com.example.solidus.domain.model.CurrencyRate
import com.example.solidus.domain.repository.CurrencyRepository
import com.example.solidus.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CurrencyRepositoryImpl(
    private val api: CurrencyApiService,
    private val dao: CurrencyRateDao,
    private val settings: SettingsRepository
) : CurrencyRepository {

    override fun getRates(): Flow<List<CurrencyRate>> {
        return dao.getAllRates().map { entities ->
            entities.map { CurrencyRate(currencyCode = it.currencyCode, rate = it.rate) }
        }
    }

    override suspend fun syncRates() = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val response = api.getLatestRates()
            val rateEntities = response.rates.map { (code, rate) ->
                CurrencyRateEntity(currencyCode = code, rate = rate)
            } + CurrencyRateEntity(currencyCode = response.base_code, rate = 1.0)
            
            dao.clearRates()
            dao.insertRates(rateEntities)
            
            settings.setLastCurrencyUpdate(System.currentTimeMillis())
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback: Exception is swallowed. App will respond to the UI exclusively via getRates() which uses cached room data!
        }
    }
}
