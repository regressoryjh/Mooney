package com.mooney.charlie

// Digunakan untuk menampilkan data di HistoryPage, dikelompokkan per tanggal
data class HistoryGroup(
    val date: String,
    val totalAmount: Long, // Total amount for that specific day
    val entries: List<FinancialEntry>
)