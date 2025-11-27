@file:OptIn(ExperimentalMaterial3Api::class)

package com.mooney.charlie

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlin.math.absoluteValue
import com.mooney.charlie.data.Entry

// Define a map for clean category-to-color mapping for the Spending Overview chart.
// Using a curated set of distinct colors for better visual separation in the donut chart.
private val CategoryColorMap = mapOf(
    "Food & Drink" to Color(0xFFF4511E), // Deep Orange
    "Transport" to Color(0xFF0288D1),    // Dark Blue
    "Bills & Utilities" to Color(0xFF4CAF50), // Green
    "Shopping" to Color(0xFF9C27B0),     // Purple
    "Household" to Color(0xFFFFC107),    // Amber
    "Entertainment" to Color(0xFFE53935), // Red
    "Investment" to Color(0xFF00ACC1),   // Cyan
    "Personal Care" to Color(0xFF8D6E63), // Brown
    "Health" to Color(0xFF3F51B5),       // Indigo
    "Education" to Color(0xFF7CB342)      // Light Green
    // Other categories will default to the theme's outline color
)

@Composable
fun HomePage(
    navController: NavHostController,
    viewModel: HomeViewModel
) {
    // Collect data from the ViewModel
    val currentBalance = viewModel.currentBalance
    val thisMonthIncome = viewModel.thisMonthIncome
    val thisMonthExpense = viewModel.thisMonthExpense
    val todayTransactions = viewModel.todayEntries
    val monthlySpendingOverview = viewModel.monthlySpendingOverview

    // Spending Overview Data Preparation
    val totalSpending = thisMonthExpense.toFloat()

    val spendingOverviewData = monthlySpendingOverview
        .map { (category, amount) ->
            val percentage = if (totalSpending > 0) (amount.toFloat() / totalSpending) * 100f else 0f
            SpendingCategory(
                name = category,
                amount = amount,
                percentage = percentage,
                // Assign a color using the centralized map, falling back to the outline color
                color = CategoryColorMap.getOrDefault(category, MaterialTheme.colorScheme.outline)
            )
        }
        .sortedByDescending { it.amount }
        .take(10) // Take Top 10 for better visibility in the chart and legend

    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "This Month's Mooney",
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
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(
                start = 28.dp,
                end = 16.dp,
                bottom = 100.dp // Reduced from 150.dp to fix the gap
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Statistics Summary (Balance Highlighted)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. PRIMARY BALANCE CARD (Larger) - Using calculated data
                    BalanceCard(
                        modifier = Modifier.fillMaxWidth(),
                        amount = formatRupiah(currentBalance),
                        color = Color(0xFF4A588B) // Highlight color

                    )

                    // 2. Income and Expense (Smaller, side-by-side) - Using calculated data
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Income Card
                        StatisticsCard(
                            modifier = Modifier.weight(1f),
                            title = "Income",
                            amount = formatRupiah(thisMonthIncome),
                            color = MaterialTheme.colorScheme.surface
                        )

                        // Expense Card
                        StatisticsCard(
                            modifier = Modifier.weight(1f),
                            title = "Expense",
                            amount = formatRupiah(thisMonthExpense),
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            }

            item {
                // Chart Card - Spending Overview (Dynamic Categories)
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Spending Overview",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
//                            IconButton(
//                                onClick = { },
//                                modifier = Modifier.size(28.dp)
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Default.MoreVert,
//                                    contentDescription = "More options",
//                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
//                                )
//                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // --- DONUT CHART IMPLEMENTATION ---
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            DonutChart(
                                data = spendingOverviewData.associate { it.name to it },
                                totalAmount = totalSpending.toLong(),
                                modifier = Modifier.size(150.dp)
                            )
                        }
                        // --- END DONUT CHART IMPLEMENTATION ---

                        Spacer(modifier = Modifier.height(24.dp))

                        // Dynamic Category indicators (up to 6, split into two columns)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            // Column 1 (Items 1-3)
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                spendingOverviewData.take(3).forEach { category ->
                                    CategoryLegendItem(
                                        title = category.name,
                                        percentage = category.percentage, // Only passing percentage now
                                        color = category.color
                                    )
                                }
                            }
                            // Column 2 (Items 4-6)
                            if (spendingOverviewData.size > 3) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    spendingOverviewData.drop(3).take(3).forEach { category ->
                                        CategoryLegendItem(
                                            title = category.name,
                                            percentage = category.percentage, // Only passing percentage now
                                            color = category.color
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                // Recent Transactions Section (Today Only)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Today Transactions",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
//                            Icon(
//                                imageVector = Icons.Default.MoreVert,
//                                contentDescription = "See all",
//                                tint = MaterialTheme.colorScheme.onSurfaceVariant
//                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

//                        Divider(
//                            modifier = Modifier.fillMaxWidth(),
//                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
//                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // FILTERED TRANSACTION LIST
                        if (todayTransactions.isEmpty()) {
                            // EMPTY STATE MESSAGE
                            Text(
                                text = "There are no transactions today.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                            )
                        } else {
                            todayTransactions.forEachIndexed { index, entry ->
                                // Note: We don't have time data, so we'll just show the date.
                                // Simplified TransactionData mapping
                                val displayTransaction = TransactionData(
                                    entry = entry, // FIX: Pass the Entry object for deletion
                                    category = entry.category,
                                    totalAmount = formatRupiah(entry.amount),
                                    // Item represents the note/description
                                    items = listOf(TransactionDetailItem(entry.note, formatRupiah(entry.amount), entry.type.toString()))
                                )

                                // ⭐ CHANGED: Using the new non-expandable component
                                TransactionListItem(
                                    transaction = displayTransaction,
                                    onDeleteClick = {
                                        viewModel.deleteEntry(it)
                                    },
                                    onEditClick = { entryToEdit ->
                                        // Navigate to Edit (New Entry page with args)
                                        navController.navigate(Destinations.NEW_WITH_ARG.replace("{${Destinations.ENTRY_ID_KEY}}", entryToEdit.id.toString()))
                                    }
                                )

                                if (index < todayTransactions.size - 1) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Divider(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
