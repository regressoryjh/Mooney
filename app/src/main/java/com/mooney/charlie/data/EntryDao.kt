package com.mooney.charlie.data

import androidx.room.*
import com.mooney.charlie.EntryType
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {
    // C R U D - Create, Update, Delete

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: Entry)

    @Update
    suspend fun update(entry: Entry)

    @Delete
    suspend fun delete(entry: Entry)

    // R E A D - With Filters

    // Read: All Entries (Unfiltered) - Used for History Page
    @Query("SELECT * FROM entry ORDER BY date DESC, id DESC")
    fun getAllEntries(): Flow<List<Entry>>

    // Read: Entries for a Specific Day
    @Query("SELECT * FROM entry WHERE date = :todayDate ORDER BY date DESC, id DESC")
    fun getEntriesForDay(todayDate: String): Flow<List<Entry>>

    // Read: Entries for a Specific Month
    @Query("SELECT * FROM entry WHERE date LIKE :monthYear || '%' ORDER BY date DESC, id DESC")
    fun getEntriesForMonth(monthYear: String): Flow<List<Entry>>

    @Query("SELECT * FROM entry WHERE id = :id")
    fun getEntryById(id: Int): Flow<Entry>

    // NEW: Suspend function for one-shot fetch (Used for Edit)
    @Query("SELECT * FROM entry WHERE id = :id")
    suspend fun getEntryByIdSuspend(id: Long): Entry?

    // Read: Entries for a Specific Month & Type (Generic)
    @Query("SELECT * FROM entry WHERE date LIKE :monthYear || '%' AND type = :type ORDER BY date DESC, id DESC")
    fun getMonthlyEntriesByType(monthYear: String, type: EntryType): Flow<List<Entry>>

    // Read: Current Balance
    @Query("""
        SELECT 
            SUM(CASE 
                    WHEN type = 'INCOME' THEN amount 
                    WHEN type = 'OUTCOME' THEN -amount 
                    ELSE 0 
                END) 
        FROM entry
    """)
    fun getCurrentBalance(): Flow<Long?>
}
