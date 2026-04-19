package com.example.solidus.data.repository

import com.example.solidus.data.local.dao.AccountDao
import com.example.solidus.data.mapper.toDomain
import com.example.solidus.data.mapper.toEntity
import com.example.solidus.domain.model.Account
import com.example.solidus.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountRepositoryImpl(
    private val dao: AccountDao
) : AccountRepository {
    override fun getAccounts(): Flow<List<Account>> {
        return dao.getAllAccounts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAccountById(id: Long): Account? {
        return dao.getAccountById(id)?.toDomain()
    }

    override suspend fun addAccount(account: Account): Long {
        return dao.insertAccount(account.toEntity())
    }

    override suspend fun updateAccount(account: Account) {
        dao.updateAccount(account.toEntity())
    }
}
