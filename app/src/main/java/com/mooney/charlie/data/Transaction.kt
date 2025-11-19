package com.mooney.charlie.data

import androidx.room.*
import java.util.UUID

//@Entity(tableName = task_table)
//data class Task(
//    // id sebagai Primary Key dan dibuat otomatis
//    @PrimaryKey(autoGenerate = true)
//    val id: Int = 0,
//
//    // Judul tugas
//    val title: String,
//
//    // Status penyelesaian tugas
//    val isCompleted: Boolean = false
//)

// 1. Data Class Definition
@Entity(tableName = "transaction_table")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: String = UUID.randomUUID().toString(), // Unique ID for list keys
    val type: EntryType, // Income or Outcome
    val category: String,
    val date: String, // YYYY-MM-DD
    val amount: Long, // Use Long for currency to avoid floating point issues
    val note: String
)

enum class EntryType {
    INCOME,
    OUTCOME
}

enum class IncomeCategory {
    Salary,
    Freelance,
    Bonus,
    Interest
}

enum class OutcomeCategory {
    FoodDrink,
    Transport,
    BillsUtilities,
    Shopping,
    Household,
    Entertainment,
    PersonalCare,
    Health,
    Education
}

