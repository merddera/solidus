package com.example.solidus.di

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.solidus.data.local.AppDatabase
import com.example.solidus.data.repository.TransactionRepositoryImpl
import com.example.solidus.domain.repository.TransactionRepository
import com.example.solidus.domain.usecase.AddTransactionUseCase
import com.example.solidus.domain.usecase.GetTransactionsUseCase
import com.example.solidus.presentation.TransactionViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val appModule = module {
    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "solidus_db"
        )
        .fallbackToDestructiveMigration()
        .addCallback(object : androidx.room.RoomDatabase.Callback() {
            override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                super.onCreate(db)
                // Insert default Categories
                db.execSQL("INSERT INTO categories (name, color, iconName, isArchived) VALUES ('Еда', '#FF5722', 'ic_food', 0)")
                db.execSQL("INSERT INTO categories (name, color, iconName, isArchived) VALUES ('Транспорт', '#2196F3', 'ic_transport', 0)")
                db.execSQL("INSERT INTO categories (name, color, iconName, isArchived) VALUES ('Зарплата', '#4CAF50', 'ic_salary', 0)")
                // Insert default Account
                db.execSQL("INSERT INTO accounts (name, balance) VALUES ('Основной счет', 0.0)")
            }
        })
        .build()
    }

    // Network
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
    }
    single { com.example.solidus.data.remote.api.CurrencyApiService(get()) }

    // DAO
    single { get<AppDatabase>().transactionDao() }
    single { get<AppDatabase>().categoryDao() }
    single { get<AppDatabase>().currencyRateDao() }
    single { get<AppDatabase>().accountDao() }

    // Repository
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    single<com.example.solidus.domain.repository.CategoryRepository> { com.example.solidus.data.repository.CategoryRepositoryImpl(get()) }
    single<com.example.solidus.domain.repository.SettingsRepository> { com.example.solidus.data.repository.SettingsRepositoryImpl(androidContext()) }
    single<com.example.solidus.domain.repository.CurrencyRepository> { com.example.solidus.data.repository.CurrencyRepositoryImpl(get(), get(), get()) }
    single<com.example.solidus.domain.repository.AccountRepository> { com.example.solidus.data.repository.AccountRepositoryImpl(get()) }

    // UseCases
    factory { com.example.solidus.domain.usecase.GetTransactionsUseCase(get()) }
    factory { com.example.solidus.domain.usecase.AddTransactionUseCase(get()) }
    factory { com.example.solidus.domain.usecase.GetTransactionByIdUseCase(get()) }
    factory { com.example.solidus.domain.usecase.UpdateTransactionUseCase(get()) }
    factory { com.example.solidus.domain.usecase.GetCategoriesUseCase(get()) }
    factory { com.example.solidus.domain.usecase.AddCategoryUseCase(get()) }
    factory { com.example.solidus.domain.usecase.ArchiveCategoryUseCase(get()) }
    factory { com.example.solidus.domain.usecase.CurrencyConverterUseCase() }

    // ViewModel
    viewModel { TransactionViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { com.example.solidus.presentation.settings.SettingsViewModel(get(), get(), get()) }

    // Worker
    worker { com.example.solidus.data.worker.CurrencySyncWorker(get(), get(), get()) }
}
