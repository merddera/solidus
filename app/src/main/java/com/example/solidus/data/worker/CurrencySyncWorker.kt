package com.example.solidus.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.solidus.domain.repository.CurrencyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CurrencySyncWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val currencyRepository: CurrencyRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            currencyRepository.syncRates()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // If it's a network error, we can retry it later
            Result.retry()
        }
    }
}
