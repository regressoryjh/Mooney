@file:OptIn(ExperimentalMaterial3Api::class)

package com.mooney.charlie

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun HistoryPage(
    navController: NavHostController,
    viewModel: HistoryViewModel
) {
    val groupedData by viewModel.groupedHistory.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Transaction History",
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 32.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(top = paddingValues.calculateTopPadding())) {

            // 2. Item List Grouped by Day
            LazyColumn(
                // ⭐ Hapus contentPadding(horizontal) di sini
                contentPadding = PaddingValues(
                    top = 0.dp,
                    bottom = 150.dp
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                if (groupedData.isEmpty()) {
                    // Use item {} for non-list content inside LazyColumn
                    item {
                        // EMPTY STATE MESSAGE
                        Text(
                            text = "There are no transactions to show.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                    }
                } else {
                    groupedData.forEach { group ->
                        // Sticky Header: Tanggal
                        stickyHeader {
                            // Header sudah memiliki padding internal (16.dp)
                            DayHeader(group.date, group.totalAmount)
                        }

                        item { Spacer(modifier = Modifier.height(6.dp)) }

                        // Daftar Entri Harian
                        itemsIndexed(group.entries) { index, entry ->
                            // ⭐ Tambahkan padding horizontal ke item agar sejajar dengan header
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                HistoryItem(
                                    entry = entry,
                                    onDeleteClick = {
                                        viewModel.deleteEntry(it)
                                    },
                                    onEditClick = { entryToEdit ->
                                        // Navigate to Edit (New Entry page with args)
                                        navController.navigate(Destinations.NEW_WITH_ARG.replace("{${Destinations.ENTRY_ID_KEY}}", entryToEdit.id.toString()))
                                    }
                                )

                                // REMOVED SEPARATOR
                                if (index < group.entries.size - 1) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    // REMOVED DIVIDER HERE
                                    // Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }

                        item { Spacer(modifier = Modifier.height(6.dp)) }
                    }
                }
            }
        }
    }
}

// Header untuk menampilkan Tanggal dan Total Harian
// HistoryPage.kt
