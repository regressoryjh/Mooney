package com.mooney.charlie

import com.mooney.charlie.data.*

// Digunakan untuk menampilkan data di HistoryPage, dikelompokkan per tanggal
data class HistoryGroup(
    val date: String,
    val totalAmount: Long, // Total amount for that specific day
    val entries: List<Entry>
)