package com.mooney.charlie

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
// FIX: Uncommented imports for data
import com.mooney.charlie.data.BudgetEntries
import com.mooney.charlie.data.EntryType
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.temporal.ChronoUnit

data class DailySpendData(
    val date: LocalDate,
    val cumulativeSpend: Long // Pengeluaran akumulatif hingga tanggal ini
)

class BudgetViewModel : ViewModel() {

    // ⭐ FIX: MOVING ALL MUTABLESTATE PROPERTIES BEFORE 'init' BLOCK ⭐

    // Status Anggaran Bulanan yang dapat diedit
    var monthlyBudget by mutableStateOf(7500000L) // Set to 4M for better testing
        private set

    // Status Pengeluaran Bulan Ini
    var totalOutcomeThisMonth by mutableStateOf(0L)
        private set

    // Status Running Balance (Sisa Anggaran)
    var remainingBudget by mutableStateOf(0L)
        private set

    // Status Data untuk Grafik Tren
    var dailySpendTrend by mutableStateOf<List<DailySpendData>>(emptyList())
        private set

    // Status Statistik Harian Baru
    var daysPassed by mutableStateOf(0)
        private set
    var daysRemaining by mutableStateOf(0)
        private set
    var dailyAverageSpend by mutableStateOf(0L)
        private set
    var recommendedDailySpend by mutableStateOf(0L)
        private set

    // The property that caused the crash (now moved up)
    var totalDaysInMonth by mutableStateOf(0)
        private set

    // ------------------------------------------------------------------

    init {
        // This is where the crash happened previously due to uninitialized properties
        calculateBudgetStats(LocalDate.now())
    }

    // Fungsi untuk memperbarui Monthly Budget
    fun updateMonthlyBudget(newAmount: Long) {
        monthlyBudget = newAmount
        // Setelah anggaran diubah, hitung ulang statistik
        calculateBudgetStats(LocalDate.now())
    }

    // Fungsi Utama untuk Menghitung Semua Statistik
    private fun calculateBudgetStats(currentDate: LocalDate) {
        val startOfMonth = currentDate.with(TemporalAdjusters.firstDayOfMonth())
        val endOfMonth = currentDate.with(TemporalAdjusters.lastDayOfMonth())

        // Perhitungan Hari
        daysPassed = startOfMonth.until(currentDate, ChronoUnit.DAYS).toInt() + 1 // Hari ke-1 hingga hari ini
        daysRemaining = currentDate.until(endOfMonth, ChronoUnit.DAYS).toInt()

        // This is the line that previously crashed, but is now safe
        totalDaysInMonth = startOfMonth.until(endOfMonth, ChronoUnit.DAYS).toInt() + 1

        // 1. Filter transaksi untuk bulan ini dan hanya ambil OUTCOME
        val thisMonthOutcomes = BudgetEntries
            .filter { entry ->
                try {
                    val entryDate = LocalDate.parse(entry.date)
                    entry.type == EntryType.OUTCOME &&
                            !entryDate.isBefore(startOfMonth) &&
                            !entryDate.isAfter(currentDate) // FIX: Only count up to currentDate for Total Outcome
                } catch (e: Exception) {
                    false
                }
            }
            .sortedBy { LocalDate.parse(it.date) } // Urutkan untuk perhitungan akumulatif

        //  Hitung Total Pengeluaran Bulan Ini
        totalOutcomeThisMonth = thisMonthOutcomes.sumOf { it.amount }
        remainingBudget = monthlyBudget - totalOutcomeThisMonth

        // 2. Perhitungan Statistik Harian BARU

        // A. Rata-Rata Pengeluaran Harian (hingga hari ini)
        dailyAverageSpend = if (daysPassed > 0) {
            totalOutcomeThisMonth / daysPassed
        } else {
            0L
        }

        // B. Rekomendasi Pengeluaran Harian (untuk sisa hari)
        recommendedDailySpend = if (daysRemaining > 0) {
            // Kita hanya bisa merekomendasikan spend jika remainingBudget > 0
            if (remainingBudget > 0) {
                remainingBudget / daysRemaining
            } else {
                0L // Jika sudah defisit, rekomendasikan 0
            }
        } else {
            0L // Hari terakhir bulan, tidak ada hari tersisa
        }

        // 3. Hitung Tren Pengeluaran Harian (Akumulatif)
        val dailySpends = mutableMapOf<LocalDate, Long>()

        // Iterate only up to the current day of the month
        for (i in 0 until currentDate.dayOfMonth) {
            val date = startOfMonth.plusDays(i.toLong())
            dailySpends[date] = 0L // Initialize spending for the day
        }

        thisMonthOutcomes.forEach { entry ->
            val date = LocalDate.parse(entry.date)
            // Ensure we only tally expenses up to today, though the filter should handle this
            if (!date.isAfter(currentDate)) {
                dailySpends[date] = dailySpends.getOrDefault(date, 0L) + entry.amount
            }
        }

        var cumulativeSpend = 0L
        dailySpendTrend = dailySpends
            .toSortedMap()
            .map { (date, spend) ->
                cumulativeSpend += spend
                DailySpendData(date, cumulativeSpend)
            }.toList()
    }
}