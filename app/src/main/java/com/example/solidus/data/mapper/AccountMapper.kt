package com.example.solidus.data.mapper

import com.example.solidus.data.local.entity.AccountEntity
import com.example.solidus.domain.model.Account

fun AccountEntity.toDomain(): Account {
    return Account(
        id = id,
        name = name,
        balance = balance
    )
}

fun Account.toEntity(): AccountEntity {
    return AccountEntity(
        id = id,
        name = name,
        balance = balance
    )
}
