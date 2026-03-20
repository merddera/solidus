package com.example.solidus.domain.repository

import com.example.solidus.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun addTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun getTransactionById(id: Long): Transaction?
    suspend fun clearAllTransactions()
    fun getTransactions(categoryId: Long?, startDate: Long?, endDate: Long?): Flow<List<Transaction>>
}
