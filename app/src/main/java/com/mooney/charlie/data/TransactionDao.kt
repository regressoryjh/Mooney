package com.mooney.charlie.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    // CREATE: Menyimpan data baru
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransaction(transaction: Transaction)

    // READ: Mengambil semua data dan mengembalikannya sebagai Flow (observasi real-time)
    @Query("SELECT * FROM transaction_table ORDER BY id DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    // READ ONLY TODAY TRANSACTION DATA shorted by newest
    @Query("SELECT * FROM transaction_table WHERE date = :todayDate ORDER BY id DESC")
    fun getTodayTransactions(todayDate: String): Flow<List<Transaction>>

    // READ ONLY THIS MONTH TRANSACTION DATA shorted by newest
    @Query("SELECT * FROM transaction_table WHERE date LIKE :monthYear || '%' ORDER BY id DESC")
    fun getMonthTransactions(monthYear: String): Flow<List<Transaction>>

    // UPDATE: Memperbarui data yang ada
    @Update
    suspend fun updateTransaction(transaction: Transaction)

    // DELETE: Menghapus data
    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
}