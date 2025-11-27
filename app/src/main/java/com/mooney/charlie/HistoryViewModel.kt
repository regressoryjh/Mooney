package com.mooney.charlie


import androidx.lifecycle.ViewModel
//import com.mooney.charlie.data.BudgetEntries // Asumsi ini adalah list data dummy Anda
//import com.mooney.charlie.data.FinancialEntry
//import com.mooney.charlie.data.HistoryGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.mooney.charlie.data.* // <--- NEW IMPORT
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Tambahkan impor untuk date/time handling
import java.util.UUID

class HistoryViewModel(private val repository: AppRepository) : ViewModel() { // <--- ADD REPOSITORY

    private val _groupedHistory = MutableStateFlow<List<HistoryGroup>>(emptyList())
    val groupedHistory: StateFlow<List<HistoryGroup>> = _groupedHistory.asStateFlow()

    init {
        // Collect All Entries flow from the repository
        viewModelScope.launch {
            repository.getAllEntries().collectLatest { entries ->
                loadHistoryWithRunningBalance(entries) // <--- Pass the fetched list
            }
        }
    }

    // Update the function to accept the list of entries
    private fun loadHistoryWithRunningBalance(allEntries: List<Entry>) {
        // 1. Siapkan List dan Urutkan Data
        // Urutkan data berdasarkan tanggal dari TERLAMA ke TERBARU
        val sortedEntries = allEntries
            .sortedWith(
                compareBy<Entry> { LocalDate.parse(it.date, DateTimeFormatter.ISO_LOCAL_DATE) }
                    .thenBy { it.id } // For stable sort on the same day
            )
            .toMutableList()

        // 2. Hitung Running Balance (Akumulasi Saldo)
        var currentRunningBalance = 0L
        val entriesWithBalance = sortedEntries.map { entry ->
            val amountEffect = if (entry.type == EntryType.INCOME) entry.amount else -entry.amount

            // Hitung saldo setelah transaksi ini
            currentRunningBalance += amountEffect

            // Buat objek sementara untuk menyimpan Running Balance
            object {
                val date = entry.date
                val finalBalance = currentRunningBalance // Saldo setelah transaksi ini
                val originalEntry = entry
            }
        }

        // 3. Kelompokkan berdasarkan tanggal (Date)
        val groupedByDate = entriesWithBalance
            .groupBy { it.date }
            .toSortedMap(compareByDescending { LocalDate.parse(it) }) // Urutkan tanggal TERBARU ke TERLAMA

        // 4. Konversi ke List<HistoryGroup> dengan Saldo Akhir Harian
        val historyList = groupedByDate.map { (date, dailyEntries) ->

            // Saldo Harian adalah Running Balance dari transaksi TERAKHIR hari itu
            val endingBalanceForDay = dailyEntries.last().finalBalance

            // Ambil kembali objek FinancialEntry asli
            val originalEntries = dailyEntries.map { it.originalEntry }

            HistoryGroup(
                date = date,
                // ⭐ Ubah totalAmount menjadi Saldo Akhir Harian
                totalAmount = endingBalanceForDay,
                entries = originalEntries.sortedByDescending { it.id } // Atau sorting yang Anda inginkan
            )
        }

        _groupedHistory.value = historyList
    }

    // Add delete functionality
    fun deleteEntry(entry: Entry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }

    // TODO: Tambahkan fungsi filter di sini (misalnya, filterByCategory, filterByMonth)
}