package com.example.solidus.domain.model

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val title: String,
    val date: Long,
    val type: TransactionType,
    val categoryId: Long? = null
)
