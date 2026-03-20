package com.example.solidus.domain.usecase

import com.example.solidus.domain.model.Transaction
import com.example.solidus.domain.repository.TransactionRepository

class GetTransactionByIdUseCase(private val repository: TransactionRepository) {
    suspend operator fun invoke(id: Long): Transaction? {
        return repository.getTransactionById(id)
    }
}
