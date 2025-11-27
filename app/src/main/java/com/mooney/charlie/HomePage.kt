@file:OptIn(ExperimentalMaterial3Api::class)

package com.mooney.charlie

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.mooney.charlie.ui.theme.CardBackgroundDark
import com.mooney.charlie.ui.theme.CardBackgroundLight


@Composable
fun getCategoryColor(rank: Int, isDark: Boolean): Color {
    // Define the start and end colors for the gradient
    // Start with a darker, richer green for the largest category
    val startColor = if (isDark) Color(0xFF388E3C) else Color(0xFF2E7D32) // Dark Green 
    // End with a very light green/yellow-green for the smallest category
    val endColor = if (isDark) Color(0xFFC8E6C9) else Color(0xFFA5D6A7)   // Very Light Green

    // Calculate the color at the given rank
    val maxRank = 5 // Top 5 categories + Others (total 6 items, ranks 0-5)
    val fraction = rank.toFloat() / maxRank.toFloat()

    // Blend the colors
    val blendedArgb = ColorUtils.blendARGB(
        startColor.toArgb(),
        endColor.toArgb(),
        fraction.coerceIn(0f, 1f)
    )

    return Color(blendedArgb)
}

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
    
    val isDark = isSystemInDarkTheme()
    val customCardColor = if (isDark) CardBackgroundDark else CardBackgroundLight

    // Spending Overview Data Preparation
    val totalSpending = thisMonthExpense.toFloat()

    // 1. Sort categories by amount (descending)
    val sortedCategories = monthlySpendingOverview.entries
        .sortedByDescending { it.value }
        .map { (category, amount) ->
            SpendingCategory(
                name = category,
                amount = amount,
                percentage = if (totalSpending > 0) (amount.toFloat() / totalSpending) * 100f else 0f,
                color = Color.Transparent // Placeholder, will be assigned later
            )
        }

    // 2. Take top 5 categories
    val topCategories = sortedCategories.take(5)

    // 3. Calculate "Others"
    val othersAmount = sortedCategories.drop(5).sumOf { it.amount }
    val othersCategory = if (othersAmount > 0) {
        SpendingCategory(
            name = "Others",
            amount = othersAmount,
            percentage = if (totalSpending > 0) (othersAmount.toFloat() / totalSpending) * 100f else 0f,
            color = Color.Transparent // Placeholder
        )
    } else null

    // 4. Combine and assign colors based on rank
    val spendingOverviewData = (topCategories + listOfNotNull(othersCategory)).mapIndexed { index, category ->
        category.copy(color = getCategoryColor(index, isDark))
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // Changed to background (Near White/Black)
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
                    .padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 36.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 150.dp
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
                        color = MaterialTheme.colorScheme.primary // UPDATED to Theme Primary

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
                            color = customCardColor
                        )

                        // Expense Card
                        StatisticsCard(
                            modifier = Modifier.weight(1f),
                            title = "Expense",
                            amount = formatRupiah(thisMonthExpense),
                            color = customCardColor
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
                        containerColor = customCardColor
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
                        containerColor = customCardColor
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
                                    // REMOVED DIVIDER HERE
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
