package com.example.solidus.data.repository

import com.example.solidus.data.local.dao.CategoryDao
import com.example.solidus.data.local.entity.CategoryEntity
import com.example.solidus.domain.model.Category
import com.example.solidus.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val dao: CategoryDao
) : CategoryRepository {
    override fun getCategories(): Flow<List<Category>> {
        return dao.getAllCategories().map { entities ->
            entities.map { Category(id = it.id, name = it.name, color = it.color, iconName = it.iconName, isArchived = it.isArchived) }
        }
    }

    override suspend fun addCategory(category: Category) {
        dao.insertCategory(CategoryEntity(name = category.name, color = category.color, iconName = category.iconName, isArchived = category.isArchived))
    }

    override suspend fun archiveCategory(categoryId: Long) {
        dao.archiveCategory(categoryId)
    }
}
