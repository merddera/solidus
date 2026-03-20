package com.example.solidus.domain.repository

import com.example.solidus.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(): Flow<List<Category>>
    suspend fun addCategory(category: Category)
    suspend fun archiveCategory(categoryId: Long)
}
