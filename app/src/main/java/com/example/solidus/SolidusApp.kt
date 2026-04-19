package com.example.solidus

import android.app.Application
import com.example.solidus.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import com.example.solidus.data.worker.CurrencySyncWorker
import java.util.concurrent.TimeUnit

class SolidusApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SolidusApp)
            workManagerFactory()
            modules(appModule)
        }
        
        setupCurrencySync()
    }

    private fun setupCurrencySync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
            
        val syncRequest = PeriodicWorkRequestBuilder<CurrencySyncWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
            
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "CurrencySync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
