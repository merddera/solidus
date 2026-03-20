package com.example.solidus.data.remote.api

import com.example.solidus.data.remote.dto.CurrencyResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class CurrencyApiService(private val client: HttpClient) {
    suspend fun getLatestRates(): CurrencyResponseDto {
        return client.get("https://open.er-api.com/v6/latest/EUR").body()
    }
}
