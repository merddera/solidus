package com.example.solidus.domain.repository

import com.example.solidus.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun addTransaction(transaction: Transaction)
    fun getTransactions(categoryId: Long? = null, startDate: Long? = null, endDate: Long? = null): Flow<List<Transaction>>
}
