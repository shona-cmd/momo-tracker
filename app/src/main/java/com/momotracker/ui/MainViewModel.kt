package com.momotracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momotracker.data.Transaction
import com.momotracker.data.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineExceptionHandler

class MainViewModel(private val repo: TransactionRepository) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _loading = MutableStateFlow<Boolean>(false)
    val loading: StateFlow<Boolean> = _loading

    init {
        viewModelScope.launch {
            _loading.value = true
            repo.allTransactions.collect {
                _transactions.value = it
                _loading.value = false
            }
        }
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println("Caught $throwable")
    }

    fun delete(tx: Transaction) {
        viewModelScope.launch(exceptionHandler) { repo.delete(tx) }
    }

    fun insert(tx: Transaction) {
        viewModelScope.launch(exceptionHandler) { repo.insert(tx) }
    }

    fun filter(predicate: (Transaction) -> Boolean) {
        viewModelScope.launch {
            _transactions.value = _transactions.value.filter(predicate)
        }
    }

    fun sort(comparator: Comparator<Transaction>) {
        viewModelScope.launch {
            _transactions.value = _transactions.value.sortedWith(comparator)
        }
    }
}
