package com.mooney.charlie.data

import androidx.room.*

@Entity(tableName = "budget_table")
data class Budget(
    // id sebagai Primary Key dan dibuat otomatis
//    @PrimaryKey(autoGenerate = true)
//    val id: Int = 0,
//
//    // Judul tugas
//    val title: String,
//
//    // Status penyelesaian tugas
//    val isCompleted: Boolean = false

    val monthlyBudget: Float
)
