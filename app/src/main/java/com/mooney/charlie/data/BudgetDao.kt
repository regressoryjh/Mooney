package com.mooney.charlie.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    // Returns a Flow which emits the budget when it changes. Returns null if not set.
    /** Read: Gets the single monthly budget. Returns null if not set. */
    @Query("SELECT * FROM monthly_budget WHERE id = 1")
    fun getBudget(): Flow<Budget?>

    // Use REPLACE to update the existing single row (with id=1) or insert it if it doesn't exist.
    // Create/Update: Inserts a new budget or replaces the existing one (due to fixed ID=1)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(budget: Budget)

    /** Delete/Reset: Deletes the single budget entry, effectively resetting it to the default (null/0) state. */
    @Query("DELETE FROM monthly_budget WHERE id = 1")
    suspend fun deleteBudget()
}