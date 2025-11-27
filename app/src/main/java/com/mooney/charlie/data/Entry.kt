package com.mooney.charlie.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mooney.charlie.EntryType // Reuse your existing enum

@Entity(tableName = "entry")
data class Entry(
    // Change: Use Long and autoGenerate for a proper primary key
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: EntryType, // INCOME or OUTCOME
    val category: String,
    val date: String, // Stored as YYYY-MM-DD
    val amount: Long,
    val note: String
)