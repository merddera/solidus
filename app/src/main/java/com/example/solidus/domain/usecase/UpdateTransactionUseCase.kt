package com.example.solidus.domain.usecase

import com.example.solidus.domain.model.Transaction
import com.example.solidus.domain.repository.TransactionRepository

class UpdateTransactionUseCase(private val repository: TransactionRepository) {
    suspend operator fun invoke(transaction: Transaction) {
        repository.updateTransaction(transaction)
    }
}
