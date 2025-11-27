package com.mooney.charlie

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mooney.charlie.data.Entry
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.OutlinedTextFieldDefaults

// --- 1. Top-Level Composable for the New Entry Form ---
@Composable
fun NewEntryForm(
    onEntrySubmitted: (Entry) -> Unit, // Callback function when save is pressed
    navController: NavHostController,
    modifier: Modifier = Modifier,
    initialEntry: Entry? = null // Optional initial entry for editing
) {
    // 2. Form State Variables
    var selectedType by rememberSaveable(initialEntry) { mutableStateOf(initialEntry?.type ?: EntryType.OUTCOME) }
    var selectedCategory by rememberSaveable(initialEntry) { mutableStateOf(initialEntry?.category ?: "") }
    var entryDate by rememberSaveable(initialEntry) { mutableStateOf(initialEntry?.date ?: java.time.LocalDate.now().toString()) }
    var amountText by rememberSaveable(initialEntry) { mutableStateOf(initialEntry?.amount?.toString() ?: "") }
    var noteText by rememberSaveable(initialEntry) { mutableStateOf(initialEntry?.note ?: "") }

    // State for Date Picker
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    // 3. Category Lists (Based on your dummy data)
    val incomeCategories = remember { listOf("Salary", "Freelance", "Bonus", "Interest") }
    val outcomeCategories = remember { listOf("Food & Drink", "Transport", "Bills & Utilities", "Shopping", "Household", "Entertainment", "Personal Care", "Health", "Investment", "Education") }

    val currentCategories = if (selectedType == EntryType.INCOME) incomeCategories else outcomeCategories

    // ⭐ Dapatkan kontrol keyboard di sini
    val keyboardController = LocalSoftwareKeyboardController.current

    // Reset selected category if type changes and old category is invalid (ONLY if not editing)
    LaunchedEffect(selectedType) {
         // Prevent category reset if it's the initial load with an existing entry
        if (initialEntry == null || initialEntry.type != selectedType) {
             if (!currentCategories.contains(selectedCategory)) {
                selectedCategory = currentCategories.firstOrNull() ?: ""
            }
        }
    }

    val isDark = isSystemInDarkTheme()
    // Match Balance Card logic: Dark Green in Dark Mode
    // Used for Buttons and Text Field Focus
    val activeColor = if (isDark) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary
    // Used for Text on Buttons
    val buttonContentColor = if (isDark) Color.White else MaterialTheme.colorScheme.onPrimary

    // Define colors for TextFields to match the theme
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = activeColor,
        focusedLabelColor = activeColor,
        cursorColor = activeColor
    )

    Surface (
        color = MaterialTheme.colorScheme.background // Changed from surface to background to match screen
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Type: Income/Outcome - Toggle Slider
            TypeToggleButton(
                selectedType = selectedType,
                onTypeSelected = { newType ->
                    // Disable changing type if editing
                    if (initialEntry == null) {
                        selectedType = newType
                    }
                },
                // NEW: Pass a flag to indicate if we are in read-only mode
                readOnly = initialEntry != null
            )

            // 2. Category: Drop Down (Conditional List)
            CategoryDropdownMenu(
                currentCategories = currentCategories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            // 3. Date: Pop Up Date Picker
            OutlinedTextField(
                value = entryDate,
                onValueChange = { }, // Read-only for manual input, controlled by picker
                label = { Text("Date (YYYY-MM-DD)") },
                trailingIcon = {
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = "Select Date",
                        modifier = Modifier.clickable { showDatePicker = true }
                    )
                },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )

            // 4. Amount in IDR: Number Keyboard
            OutlinedTextField(
                value = amountText,
                onValueChange = { newValue ->
                    // Filter to allow only numbers
                    amountText = newValue.filter { it.isDigit() }
                },
                label = { Text("Amount (IDR)") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword, // Trigger number keyboard
                    imeAction = ImeAction.Next
                ),
                leadingIcon = { Text("Rp") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )

            // 5. Entry Note: Character Limit
            OutlinedTextField(
                value = noteText,
                onValueChange = { newValue ->
                    // Limit character count (e.g., 100 characters)
                    if (newValue.length <= 100) {
                        noteText = newValue
                    }
                },
                label = { Text("Entry Note (Max 100 chars)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 4,
                supportingText = {
                    Text("${noteText.length} / 100")
                },
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Save Button
            Button(
                onClick = {
                    val amountLong = amountText.toLongOrNull()
                    if (amountLong != null && amountLong > 0) {
                        val newEntry = Entry(
                            id = initialEntry?.id ?: 0L, // Preserve ID if editing
                            type = selectedType,
                            category = selectedCategory,
                            date = entryDate,
                            amount = amountLong,
                            note = noteText
                        )
                        onEntrySubmitted(newEntry)
                    } else {
                        // Show error (e.g., via Snackbar)
                    }// ⭐ LANGKAH KRUSIAL: Sembunyikan keyboard sebelum navigasi
                    keyboardController?.hide()

                    navController.popBackStack()
                },
                enabled = amountText.isNotEmpty() && selectedCategory.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = activeColor,
                    contentColor = buttonContentColor
                )
            ) {
                Text(if (initialEntry == null) "SAVE ENTRY" else "SAVE EDIT")
            }
            // Cancel Button
            Button(
                onClick = {
                    // ⭐ LANGKAH KRUSIAL: Sembunyikan keyboard sebelum navigasi
                    keyboardController?.hide()
                    navController.popBackStack()

                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = activeColor,
                    contentColor = buttonContentColor
                )
            ) {
                Text("CANCEL")
            }
        }
    }

    // Date Picker Dialog (Appears on top of the screen)
    if (showDatePicker) {
        DatePickerDialog(
            initialDate = entryDate,
            onDateSelected = { newDate -> entryDate = newDate },
            onDismiss = { showDatePicker = false }
        )
    }
}
