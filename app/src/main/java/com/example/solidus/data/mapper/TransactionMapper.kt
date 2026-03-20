package com.example.solidus.data.mapper

import com.example.solidus.data.local.entity.TransactionEntity
import com.example.solidus.domain.model.Transaction

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        amount = amount,
        title = title,
        date = date,
        type = type
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        amount = amount,
        title = title,
        date = date,
        type = type
    )
}
