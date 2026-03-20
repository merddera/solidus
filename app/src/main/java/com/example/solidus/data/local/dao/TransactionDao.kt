package com.example.solidus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.solidus.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE (:categoryId IS NULL OR categoryId = :categoryId) AND (:startDate IS NULL OR date >= :startDate) AND (:endDate IS NULL OR date <= :endDate) ORDER BY date DESC")
    fun getFilteredTransactions(categoryId: Long?, startDate: Long?, endDate: Long?): Flow<List<TransactionEntity>>
}
