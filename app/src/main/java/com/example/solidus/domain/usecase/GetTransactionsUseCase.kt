package com.example.solidus.domain.usecase

import com.example.solidus.domain.model.Transaction
import com.example.solidus.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class GetTransactionsUseCase(
    private val repository: TransactionRepository
) {
    operator fun invoke(categoryId: Long? = null, startDate: Long? = null, endDate: Long? = null): Flow<List<Transaction>> {
        return repository.getTransactions(categoryId, startDate, endDate)
    }
}
