package com.mooney.charlie

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mooney.charlie.data.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: AppRepository) : ViewModel() {

    // Live/Observable Data for UI
    var currentBalance by mutableStateOf(0L)
        private set
    var todayEntries by mutableStateOf<List<Entry>>(emptyList())
        private set
    var monthlySpendingOverview by mutableStateOf<Map<String, Long>>(emptyMap())
        private set
    var monthlyBudget by mutableStateOf(0L)
        private set
    var remainingBudget by mutableStateOf(0L)
        private set
    var thisMonthIncome by mutableStateOf(0L)
        private set
    var thisMonthExpense by mutableStateOf(0L)
        private set

    init {
        // Collect Current Balance
        viewModelScope.launch {
            repository.getCurrentBalance().collectLatest { balance ->
                currentBalance = balance ?: 0L
            }
        }

        // Collect Today's Entries
        viewModelScope.launch {
            repository.getEntriesForToday().collectLatest { entries ->
                todayEntries = entries
            }
        }

        // Collect Monthly Budget
        viewModelScope.launch {
            repository.getBudget().collectLatest { budget ->
                monthlyBudget = budget?.amount ?: 0L
                // Re-calculate remaining budget when budget changes
                remainingBudget = monthlyBudget - thisMonthExpense
            }
        }

        // Collect Monthly Expenses
        viewModelScope.launch {
            repository.getMonthlyExpenses().collectLatest { expenses ->
                thisMonthExpense = expenses.sumOf { it.amount }
                updateSpendingStats(expenses)
            }
        }

        // Collect Monthly Income
        viewModelScope.launch {
            repository.getMonthlyIncomes().collectLatest { incomes ->
                thisMonthIncome = incomes.sumOf { it.amount }
            }
        }
    }

    // Helper function to update spending overview and remaining budget
    private fun updateSpendingStats(monthlyExpenses: List<Entry>) {
        remainingBudget = monthlyBudget - thisMonthExpense

        // Calculate category breakdown
        monthlySpendingOverview = monthlyExpenses
            .groupBy { it.category }
            .mapValues { it.value.sumOf { entry -> entry.amount } }
            .toList()
            .sortedByDescending { it.second }
            .toMap()
    }

    fun deleteEntry(entry: Entry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }
}
