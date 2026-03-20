package com.example.solidus.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CurrencyResponseDto(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
