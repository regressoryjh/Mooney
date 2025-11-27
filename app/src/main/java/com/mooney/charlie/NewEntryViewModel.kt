package com.mooney.charlie

// AndroidX Architecture Components
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch // Import launch

// Your App's Data Structure
import com.mooney.charlie.data.*

// NewEntryViewModel.kt (Part 1/3 - Data)
data class EntryUiState(
    val id: Long = 0,
    val type: EntryType = EntryType.OUTCOME,
    val category: String = "",
    val date: String = getCurrentDate(),
    val amount: String = "",
    val note: String = "",
    val isEntryValid: Boolean = false
)

// Extension function to convert UI state to Room entity
fun EntryUiState.toEntry(): Entry = Entry(
    id = this.id,
    type = this.type,
    category = this.category,
    date = this.date,
    amount = this.amount.toLongOrNull() ?: 0L,
    note = this.note
)

// NewEntryViewModel.kt (Part 2/3 - ViewModel)
class NewEntryViewModel(private val appRepository: AppRepository) : ViewModel() {

    var entryUiState by mutableStateOf(EntryUiState())
        private set

    fun updateUiState(newEntryUiState: EntryUiState) {
        entryUiState = newEntryUiState.copy(isEntryValid = validateInput(newEntryUiState))
    }

    // Save from internal state
    fun saveEntry() {
        if (!entryUiState.isEntryValid) return

        val entryToSave = entryUiState.toEntry()
        saveEntry(entryToSave)
    }

    // Save passed entry directly (For integration with existing NewEntryForm)
    fun saveEntry(entry: Entry) {
        viewModelScope.launch {
            // Core Logic: Insert vs. Update
            if (entry.id != 0L) {
                appRepository.updateEntry(entry)
            } else {
                appRepository.insertEntry(entry)
            }
        }
    }

    private fun validateInput(uiState: EntryUiState): Boolean {
        return uiState.category.isNotBlank() &&
                uiState.amount.toLongOrNull() != null &&
                (uiState.amount.toLongOrNull() ?: 0L) > 0L
    }
}
