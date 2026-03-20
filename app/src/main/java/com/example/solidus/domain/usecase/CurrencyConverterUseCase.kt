package com.example.solidus.domain.usecase

import com.example.solidus.domain.model.CurrencyRate

class CurrencyConverterUseCase {
    fun convert(amount: Double, fromCurrency: String, toCurrency: String, rates: List<CurrencyRate>): Double {
        if (fromCurrency == toCurrency) return amount
        if (rates.isEmpty()) return amount

        val fromRate = rates.find { it.currencyCode == fromCurrency }?.rate ?: 1.0
        val toRate = rates.find { it.currencyCode == toCurrency }?.rate ?: 1.0
        
        // Assuming rates are based on a common denominator (e.g. EUR = 1.0)
        val amountInBase = amount / fromRate
        return amountInBase * toRate
    }
}
