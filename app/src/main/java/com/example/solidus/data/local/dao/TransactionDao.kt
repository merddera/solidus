package com.example.solidus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.solidus.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Query("DELETE FROM transactions")
    suspend fun clearAll()

    @Query("DELETE FROM categories WHERE name NOT IN ('Еда', 'Транспорт', 'Зарплата')")
    suspend fun clearUserCategories()

    @Query("SELECT * FROM transactions WHERE (:categoryId IS NULL OR categoryId = :categoryId) AND (:startDate IS NULL OR date >= :startDate) AND (:endDate IS NULL OR date <= :endDate) ORDER BY date DESC")
    fun getFilteredTransactions(categoryId: Long?, startDate: Long?, endDate: Long?): Flow<List<TransactionEntity>>
}
