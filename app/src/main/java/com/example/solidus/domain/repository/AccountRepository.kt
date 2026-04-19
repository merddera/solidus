package com.example.solidus.domain.repository

import com.example.solidus.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccounts(): Flow<List<Account>>
    suspend fun getAccountById(id: Long): Account?
    suspend fun addAccount(account: Account): Long
    suspend fun updateAccount(account: Account)
}
