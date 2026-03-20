package com.example.solidus.domain.usecase

import com.example.solidus.domain.model.Category
import com.example.solidus.domain.repository.CategoryRepository

class AddCategoryUseCase(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(name: String, color: String) {
        repository.addCategory(Category(name = name, color = color))
    }
}
