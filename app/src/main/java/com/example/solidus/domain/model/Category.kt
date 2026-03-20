package com.example.solidus.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val color: String,
    val isArchived: Boolean = false
)
