package com.momotracker.data

import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TxDao) {
    val allTransactions: Flow<List<Transaction>> = dao.getAll()

    suspend fun insert(tx: Transaction) = dao.insert(tx)
    suspend fun delete(tx: Transaction) = dao.delete(tx)
}
