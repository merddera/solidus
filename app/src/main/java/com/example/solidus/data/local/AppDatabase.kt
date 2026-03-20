package com.example.solidus.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.solidus.data.local.dao.TransactionDao
import com.example.solidus.data.local.entity.TransactionEntity

import com.example.solidus.data.local.dao.CategoryDao
import com.example.solidus.data.local.entity.CategoryEntity
import com.example.solidus.data.local.dao.CurrencyRateDao
import com.example.solidus.data.local.entity.CurrencyRateEntity

@Database(entities = [TransactionEntity::class, CategoryEntity::class, CurrencyRateEntity::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun currencyRateDao(): CurrencyRateDao
}
