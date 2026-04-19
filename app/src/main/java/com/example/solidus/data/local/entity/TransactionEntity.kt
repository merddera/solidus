package com.example.solidus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.solidus.domain.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val title: String,
    val date: Long,
    val type: TransactionType,
    val categoryId: Long? = null,
    val accountId: Long,
    val currencyCode: String = "RUB"
)
