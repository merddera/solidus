package com.example.solidus

import android.app.Application
import com.example.solidus.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SolidusApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SolidusApp)
            modules(appModule)
        }
    }
}
