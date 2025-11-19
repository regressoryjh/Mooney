package com.mooney.charlie.data

import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val budgetDao: BudgetDao) {

    // Fungsi untuk Read: Room sudah menyediakan Flow
    val allBudgets: Flow<List<Budget>> = budgetDao.getAllBudgets()

    // Fungsi untuk Create: dipanggil dalam Coroutine
    suspend fun insert(budget: Budget) {
        budgetDao.insertBudget(budget)
    }

    // Fungsi untuk Update: dipanggil dalam Coroutine
    suspend fun update(budget: Budget) {
        budgetDao.updateBudget(budget)
    }

    // Fungsi untuk Delete: dipanggil dalam Coroutine
    suspend fun delete(budget: Budget) {
        budgetDao.deleteBudget(budget)
    }
}