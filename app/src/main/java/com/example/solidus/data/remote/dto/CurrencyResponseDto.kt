package com.example.solidus.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CurrencyResponseDto(
    val base_code: String,
    val rates: Map<String, Double>
)
