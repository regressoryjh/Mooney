package com.mooney.charlie.data

//import androidx.room.*
//import kotlinx.coroutines.flow.Flow
//
//@Dao
//interface BudgetDao {
//    // CREATE: Menyimpan data baru
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insertBudget(budget: Budget)
//
//    // READ: Mengambil semua data dan mengembalikannya sebagai Flow (observasi real-time)
//    @Query("SELECT * FROM budget_table")
//    fun getAllBudgets(): Flow<List<Budget>>
//
//    // UPDATE: Memperbarui data yang ada
//    @Update
//    suspend fun updateBudget(budget: Budget)
//
//    // DELETE: Menghapus data
//    @Delete
//    suspend fun deleteBudget(budget: Budget)
//}