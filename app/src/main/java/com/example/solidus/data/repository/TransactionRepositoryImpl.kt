package com.example.solidus.data.repository

import com.example.solidus.data.local.dao.TransactionDao
import com.example.solidus.data.mapper.toDomain
import com.example.solidus.data.mapper.toEntity
import com.example.solidus.domain.model.Transaction
import com.example.solidus.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(
    private val dao: TransactionDao
) : TransactionRepository {

    override suspend fun addTransaction(transaction: Transaction) {
        dao.insert(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        dao.update(transaction.toEntity())
    }

    override suspend fun getTransactionById(id: Long): Transaction? {
        return dao.getTransactionById(id)?.toDomain()
    }

    override suspend fun clearAllTransactions() {
        dao.clearAll()
        dao.clearUserCategories()
    }

    override fun getTransactions(categoryId: Long?, startDate: Long?, endDate: Long?): Flow<List<Transaction>> {
        return dao.getFilteredTransactions(categoryId, startDate, endDate).map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
