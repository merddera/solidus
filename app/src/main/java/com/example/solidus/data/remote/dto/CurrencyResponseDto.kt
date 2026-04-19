package com.example.solidus.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrencyResponseDto(
    @SerialName("base_code")
    val baseCode: String,
    val rates: Map<String, Double>
)
