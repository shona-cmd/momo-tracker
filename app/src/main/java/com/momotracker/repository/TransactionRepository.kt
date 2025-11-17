package com.momotracker.repository

import com.momotracker.data.Transaction
import com.momotracker.data.TransactionDao
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TransactionDao) {
    val allTransactions: Flow<List<Transaction>> = dao.getAll()
    val totalReceived = dao.totalReceived()
    val totalSent = dao.totalSent()

    suspend fun insert(transaction: Transaction) {
        dao.insert(transaction)
    }
}
