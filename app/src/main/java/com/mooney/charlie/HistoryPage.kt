@file:OptIn(ExperimentalMaterial3Api::class)

package com.mooney.charlie

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

@Composable
fun HistoryPage(
    navController: NavHostController,
    viewModel: HistoryViewModel
) {
    val groupedData by viewModel.groupedHistory.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
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
                    .padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 32.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            // 2. Item List Grouped by Day
            LazyColumn(
                // ⭐ Hapus contentPadding(horizontal) di sini
                contentPadding = PaddingValues(top = 0.dp),
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

                        // Daftar Entri Harian
                        items(group.entries) { entry ->
                            // ⭐ Tambahkan padding horizontal ke item agar sejajar dengan header
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                HistoryItem(
                                    entry = entry,
                                    onDeleteClick = {
                                        viewModel.deleteEntry(it)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Header untuk menampilkan Tanggal dan Total Harian
// HistoryPage.kt

@Composable
fun DayHeader(date: String, totalAmount: Long) {
    val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))

    // Tentukan tanda (minus atau kosong) di sini
    val sign = if (totalAmount < 0) "-" else ""

    // Gunakan nilai absolut (abs) untuk memformat angka tanpa tanda
    val formattedTotal = numberFormat.format(abs(totalAmount))

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Tanggal
            Text(
                text = date,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // ⭐ TOTAL HARIAN YANG DIFORMAT ULANG ⭐
            Text(
                text = "$sign Rp$formattedTotal", // Tanda kini di depan Rp
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
//                color = if (totalAmount >= 0) Color(0xFF1B5E20) else Color(0xFFB71C1C)
            )
        }
    }
}
