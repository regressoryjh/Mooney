package com.mooney.charlie

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue
import com.mooney.charlie.data.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

data class DailySpendData(
    val date: LocalDate,
    val cumulativeSpend: Long // Pengeluaran akumulatif hingga tanggal ini
)

class BudgetViewModel(private val repository: AppRepository) : ViewModel() {

    // The mutableStateOf properties are still needed for Compose, but they are now updated by Flow collectors.
    var monthlyBudget by mutableStateOf(0L)
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
        // 1. Collect Budget Flow
        viewModelScope.launch {
            repository.getBudget().collectLatest { budget ->
                monthlyBudget = budget?.amount ?: 0L
                // Trigger recalculation if we have entries (though entries flow will also trigger it)
                // For now, we rely on entries flow or re-fetching if needed, but usually entries flow is enough
            }
        }

        // 2. Collect Monthly Entries Flow (Incomes and Outcomes)
        viewModelScope.launch {
            repository.getEntriesForCurrentMonth().collectLatest { entries ->
                // Recalculate all stats whenever entries change.
                recalculateBudgetStats(entries)
            }
        }
    }

    // Function to update the budget using the repository
    fun updateMonthlyBudget(amount: Long) {
        viewModelScope.launch {
            repository.updateMonthlyBudget(amount)
        }
    }

    // Fungsi Utama untuk Menghitung Semua Statistik
    private fun recalculateBudgetStats(thisMonthEntries: List<Entry>) {
        val currentDate = LocalDate.now()
        val startOfMonth = currentDate.with(TemporalAdjusters.firstDayOfMonth())
        val endOfMonth = currentDate.with(TemporalAdjusters.lastDayOfMonth())

        // Update Date Stats
        totalDaysInMonth = endOfMonth.dayOfMonth
        daysPassed = currentDate.dayOfMonth
        daysRemaining = totalDaysInMonth - daysPassed

        // 1. Filter transaksi untuk bulan ini dan hanya ambil OUTCOME
        val thisMonthOutcomes = thisMonthEntries.filter { it.type == EntryType.OUTCOME }

        // 2. Hitung Total Outcome dan Sisa Anggaran
        totalOutcomeThisMonth = thisMonthOutcomes.sumOf { it.amount }
        remainingBudget = monthlyBudget - totalOutcomeThisMonth

        // 3. Perhitungan Statistik Harian BARU

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

        // 4. Hitung Tren Pengeluaran Harian (Akumulatif)
        val dailySpends = mutableMapOf<LocalDate, Long>()

        // Initialize all days up to today with 0
        for (i in 0 until currentDate.dayOfMonth) {
            val date = startOfMonth.plusDays(i.toLong())
            dailySpends[date] = 0L 
        }

        thisMonthOutcomes.forEach { entry ->
             // Assuming entry.date is YYYY-MM-DD
             try {
                 val date = LocalDate.parse(entry.date)
                 // Ensure we only tally expenses up to today (though filter handles month)
                 if (!date.isAfter(currentDate)) {
                     dailySpends[date] = dailySpends.getOrDefault(date, 0L) + entry.amount
                 }
             } catch (e: Exception) {
                 // Handle parse error if necessary
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
