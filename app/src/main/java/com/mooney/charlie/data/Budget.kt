package com.mooney.charlie.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthly_budget")
data class Budget(
    // Use a fixed ID (1) so Room knows to only keep one row for the budget
    @PrimaryKey
    val id: Int = 1,
    val amount: Long
)