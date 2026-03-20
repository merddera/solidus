package com.example.solidus.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.solidus.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {

    companion object {
        val SELECTED_CURRENCY_KEY = stringPreferencesKey("selected_currency")
        val HIDE_BALANCE_KEY = booleanPreferencesKey("hide_balance")
        val LAST_CURRENCY_UPDATE_KEY = longPreferencesKey("last_currency_update")
    }

    override val selectedCurrency: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[SELECTED_CURRENCY_KEY] ?: "RUB"
        }

    override val hideBalance: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[HIDE_BALANCE_KEY] ?: false
        }

    override val lastCurrencyUpdate: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_CURRENCY_UPDATE_KEY] ?: 0L
        }

    override suspend fun setSelectedCurrency(currencyCode: String) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_CURRENCY_KEY] = currencyCode
        }
    }

    override suspend fun setHideBalance(hide: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HIDE_BALANCE_KEY] = hide
        }
    }

    override suspend fun setLastCurrencyUpdate(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[LAST_CURRENCY_UPDATE_KEY] = timestamp
        }
    }
}
