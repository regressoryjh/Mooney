@file:OptIn(ExperimentalMaterial3Api::class)

package com.mooney.charlie

// Layouts and UI
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
// Compose Core and State
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
// Material 3 Components
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import java.util.Calendar
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Color


// ui/components/NewEntryComponents.kt

@Composable
fun TypeToggleButton(
    selectedType: EntryType,
    onTypeSelected: (EntryType) -> Unit,
    readOnly: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EntryType.entries.forEach { type ->
            val isSelected = selectedType == type
            // If readOnly is true, determine colors based on whether this button is the selected one.
            // The unselected button will get a "disabled" look.
            val containerColor = if (readOnly) {
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) // Grayed out selected
                else MaterialTheme.colorScheme.background // Screen background color for unselected read-only
            } else {
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background // Screen background for unselected
            }

            val contentColor = if (readOnly) {
                if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            } else {
                if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            }

            OutlinedButton(
                onClick = { if (!readOnly) onTypeSelected(type) },
                // Make it appear unclickable if readOnly (though onClick handles the logic)
                enabled = !readOnly, 
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = containerColor,
                    contentColor = contentColor,
                    disabledContainerColor = containerColor, // Keep custom color even when disabled
                    disabledContentColor = contentColor
                ),
                modifier = Modifier.weight(1f)
            ) {
                Text(type.name)
            }
        }
    }
}

// ui/components/NewEntryComponents.kt

@Composable
fun CategoryDropdownMenu(
    currentCategories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            currentCategories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

// ui/components/NewEntryComponents.kt

@Composable
fun DatePickerDialog(
    initialDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val date = remember {
        val today = java.time.LocalDate.parse(initialDate)
        Calendar.getInstance().apply {
            set(today.year, today.monthValue - 1, today.dayOfMonth)
        }.timeInMillis
    }

    // Compose's built-in DatePicker
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date
    )

    // We use a Material3 AlertDialog to host the DatePicker
    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selected = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                            .toString()
                        onDateSelected(selected)
                    }
                    onDismiss()
                }
            ) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
