package com.momotracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.momotracker.data.Transaction
import com.momotracker.db.TransactionDatabase
import com.momotracker.repository.TransactionRepository
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransactionRepository

    val allTransactions = repository.allTransactions
    val totalReceived = repository.totalReceived
    val totalSent = repository.totalSent

    init {
        val dao = TransactionDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(dao)
    }

    fun insert(transaction: Transaction) = viewModelScope.launch {
        repository.insert(transaction)
    }
}
