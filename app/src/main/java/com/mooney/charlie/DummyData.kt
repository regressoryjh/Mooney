@file:Suppress("DEPRECATION")

package com.mooney.charlie

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

//// 1. Data Class Definition
//data class FinancialEntry(
//    val id: String = UUID.randomUUID().toString(), // Unique ID for list keys
//    val type: EntryType, // Income or Outcome
//    val category: String,
//    val date: String, // YYYY-MM-DD
//    val amount: Long, // Use Long for currency to avoid floating point issues
//    val note: String
//) not used, use the one in data package

enum class EntryType {
    INCOME,
    OUTCOME
}


// 2. Formatting Helpers
// Use Indonesian Rupiah format
private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
    maximumFractionDigits = 0
}

/** Formats a Long amount into Indonesian Rupiah (e.g., "Rp 15.000.000") */
fun formatRupiah(amount: Long): String {
    return currencyFormat.format(amount.absoluteValue)
}

/** Gets the current date formatted as YYYY-MM-DD */
fun getCurrentDate(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}

/** Gets the current month and year formatted as YYYY-MM */
fun getCurrentMonthYear(): String {
    return SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
}