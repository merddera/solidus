package com.example.solidus.data.repository

import com.example.solidus.data.local.dao.TransactionDao
import com.example.solidus.data.mapper.toDomain
import com.example.solidus.data.mapper.toEntity
import com.example.solidus.domain.model.Transaction
import com.example.solidus.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {

    override suspend fun addTransaction(transaction: Transaction) {
        dao.insert(transaction.toEntity())
    }

    override fun getTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
