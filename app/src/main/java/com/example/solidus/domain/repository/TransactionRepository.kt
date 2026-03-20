package com.example.solidus.domain.repository

import com.example.solidus.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun addTransaction(transaction: Transaction)
    fun getTransactions(): Flow<List<Transaction>>
}
