@file:OptIn(ExperimentalMaterial3Api::class)

package com.mooney.charlie

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.compose.ui.unit.dp
import com.mooney.charlie.data.Entry

@Composable
fun NewEntry(
    navController: NavHostController,
    viewModel: NewEntryViewModel,
    initialEntry: Entry? = null // Accept an optional entry for editing
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column (modifier = Modifier.padding(paddingValues)) {
            // Header
            Text(
                text = if (initialEntry == null) "New Entry" else "Edit Entry",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 24.dp),
                // dont change the padding its my prefered padding
            )
            NewEntryForm(
                onEntrySubmitted = { newEntry ->
                    // Save entry using ViewModel
                    viewModel.saveEntry(newEntry)
                },
                navController = navController,
                modifier = Modifier.fillMaxSize(),
                initialEntry = initialEntry // Pass the entry to the form
            )
        }
    }
}
