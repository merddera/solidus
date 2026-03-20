package com.example.solidus.domain.usecase

import com.example.solidus.domain.model.Transaction
import com.example.solidus.domain.repository.TransactionRepository

class AddTransactionUseCase(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        if (transaction.amount <= 0) {
            throw IllegalArgumentException("Amount must be greater than 0")
        }
        if (transaction.title.isBlank()) {
            throw IllegalArgumentException("Title cannot be empty")
        }
        repository.addTransaction(transaction)
    }
}
