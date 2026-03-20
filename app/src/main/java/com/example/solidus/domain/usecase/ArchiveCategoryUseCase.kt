package com.example.solidus.domain.usecase

import com.example.solidus.domain.repository.CategoryRepository

class ArchiveCategoryUseCase(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(categoryId: Long) {
        repository.archiveCategory(categoryId)
    }
}
