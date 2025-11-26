@file:OptIn(ExperimentalMaterial3Api::class)

package com.mooney.charlie

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun NewEntry(navController: NavHostController) {
//    val viewModel: HomeViewModel = viewModel()
    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column (modifier = Modifier.padding(paddingValues)) {
            // Header
            Text(
                text = "New Entry",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
            )
            NewEntryForm(
                onEntrySubmitted = { newEntry ->
                    println("New Entry Submitted: $newEntry")
                },
                navController,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// --- 1. Top-Level Composable for the New Entry Form ---
@Composable
fun NewEntryForm(
    onEntrySubmitted: (FinancialEntry) -> Unit, // Callback function when save is pressed
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // 2. Form State Variables
    var selectedType by rememberSaveable { mutableStateOf(EntryType.OUTCOME) }
    var selectedCategory by rememberSaveable { mutableStateOf("") }
    var entryDate by rememberSaveable { mutableStateOf(java.time.LocalDate.now().toString()) }
    var amountText by rememberSaveable { mutableStateOf("") }
    var noteText by rememberSaveable { mutableStateOf("") }

    // State for Date Picker
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    // 3. Category Lists (Based on your dummy data)
    val incomeCategories = remember { listOf("Salary", "Freelance", "Bonus", "Interest") }
    val outcomeCategories = remember { listOf("Food & Drink", "Transport", "Bills & Utilities", "Shopping", "Household", "Entertainment", "Personal Care", "Health", "Investment", "Education") }

    val currentCategories = if (selectedType == EntryType.INCOME) incomeCategories else outcomeCategories

    // ⭐ Dapatkan kontrol keyboard di sini
    val keyboardController = LocalSoftwareKeyboardController.current

    // Reset selected category if type changes and old category is invalid
    LaunchedEffect(selectedType) {
        if (!currentCategories.contains(selectedCategory)) {
            selectedCategory = currentCategories.firstOrNull() ?: ""
        }
    }

    Surface (
        color = MaterialTheme.colorScheme.secondaryContainer
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
                onTypeSelected = { newType -> selectedType = newType }
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
                modifier = Modifier.fillMaxWidth()
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
                modifier = Modifier.fillMaxWidth()
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
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Save Button
            Button(
                onClick = {
                    val amountLong = amountText.toLongOrNull()
                    if (amountLong != null && amountLong > 0) {
                        val newEntry = FinancialEntry(
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
//
//                        // popUpTo: Kembali ke ID destinasi awal NavGraph Anda
//                        popUpTo(navController.graph.startDestinationId) {
//                            // inclusive = true: Hapus destinasi "New Entry" dari tumpukan
//                            inclusive = true
//                        }
//                        // launchSingleTop = true: Cegah pembuatan instance 'Home' baru jika sudah ada di tumpukan
//                        launchSingleTop = true
//                    }

                },
                enabled = amountText.isNotEmpty() && selectedCategory.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("SAVE ENTRY")
            }
            // Cancel Button
            Button(
                onClick = {
                    // ⭐ LANGKAH KRUSIAL: Sembunyikan keyboard sebelum navigasi
                    keyboardController?.hide()
                    navController.popBackStack()

                },
                modifier = Modifier.fillMaxWidth()
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