package com.example.solidus.domain.usecase

import com.example.solidus.domain.repository.CategoryRepository
import com.example.solidus.domain.model.Category
import kotlinx.coroutines.flow.Flow

class GetCategoriesUseCase(private val repository: CategoryRepository) {
    operator fun invoke(): Flow<List<Category>> {
        return repository.getCategories()
    }
}
