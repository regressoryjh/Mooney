package com.mooney.charlie.data

import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    // Fungsi untuk Read: Room sudah menyediakan Flow
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()

    // Fungsi untuk Create: dipanggil dalam Coroutine
    suspend fun insert(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    // Fungsi untuk Update: dipanggil dalam Coroutine
    suspend fun update(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }

    // Fungsi untuk Delete: dipanggil dalam Coroutine
    suspend fun delete(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }
}