package com.mooney.charlie.data

import com.mooney.charlie.EntryType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AppRepository(
    private val entryDao: EntryDao,
    private val budgetDao: BudgetDao
) {
    // --- Financial Entry Operations ---
    // READ: All entries
    fun getAllEntries(): Flow<List<Entry>> = entryDao.getAllEntries()

    // READ: This Month only (Calculates 'YYYY-MM' automatically)
    fun getEntriesForCurrentMonth(): Flow<List<Entry>> {
        val currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
        return entryDao.getEntriesForMonth(currentMonth)
    }

    // READ: Today only (Calculates 'YYYY-MM-DD' automatically)
    fun getEntriesForToday(): Flow<List<Entry>> {
        val todayDate = LocalDate.now().toString()
        return entryDao.getEntriesForDay(todayDate)
    }

    // READ: Single Entry by ID
    fun getEntryById(id: Int): Flow<Entry> {
        return entryDao.getEntryById(id)
    }

    // READ: Single Entry by ID (Suspend for Edit)
    suspend fun getEntryByIdSuspend(id: Long): Entry? {
        return entryDao.getEntryByIdSuspend(id)
    }

    private fun getCurrentMonthString(): String {
        // Helper function to calculate 'YYYY-MM'
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    }

    // READ: This Month only, Filtered by Type: EXPENSE
    fun getMonthlyExpenses(): Flow<List<Entry>> {
        val currentMonth = getCurrentMonthString()
        // Use generic method with OUTCOME type
        return entryDao.getMonthlyEntriesByType(currentMonth, EntryType.OUTCOME)
    }

    // READ: This Month only, Filtered by Type: INCOME
    fun getMonthlyIncomes(): Flow<List<Entry>> {
        val currentMonth = getCurrentMonthString()
        // Use generic method with INCOME type
        return entryDao.getMonthlyEntriesByType(currentMonth, EntryType.INCOME)
    }

    // READ: Current Balance (All Time Income - All Time Expenses)
    fun getCurrentBalance(): Flow<Long?> {
        return entryDao.getCurrentBalance()
    }

    // CREATE/EDIT/DELETE
    suspend fun insertEntry(entry: Entry) = entryDao.insert(entry)
    suspend fun updateEntry(entry: Entry) = entryDao.update(entry)
    suspend fun deleteEntry(entry: Entry) = entryDao.delete(entry)

    // --- BUDGET OPERATIONS ---

    // READ: Returns the Budget. It will always return a Flow that eventually yields
    // the default 0 or the user-set value due to the database callback.
    fun getBudget(): Flow<Budget?> = budgetDao.getBudget()

    // EDIT (User sets the budget manually)
    suspend fun updateMonthlyBudget(amount: Long) {
        // Creates a Budget object with the fixed ID=1 to trigger REPLACE
        val budget = Budget(amount = amount)
        budgetDao.insertOrUpdate(budget)
    }

    // DELETE (Resets the budget, though the default of 0 will still be present after DB creation)
    // If you want "Delete" to explicitly set it back to 0, use updateMonthlyBudget(0L) instead
    // of calling this delete method. I recommend using the update approach for better control.
    suspend fun deleteMonthlyBudget() {
        budgetDao.deleteBudget()
    }
}
