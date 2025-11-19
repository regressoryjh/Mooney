@file:OptIn(ExperimentalMaterial3Api::class)

package com.mooney.charlie

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
fun HomePage(navController: NavHostController) {

    // --- START: DYNAMIC DATA CALCULATION ---
    // 1. Get current date/month dynamically
    val currentMonthYear = getCurrentMonthYear() // e.g., "2025-11"
    val todayDate = getCurrentDate()           // e.g., "2025-11-25"

    val currentMonthEntries = BudgetEntries.filter { it.date.startsWith(currentMonthYear) }

    // 2. Calculate Totals for the Current Month
    val totalIncome = BudgetEntries
        .filter { it.type == EntryType.INCOME }
        .sumOf { it.amount }

    val totalExpense = BudgetEntries
        .filter { it.type == EntryType.OUTCOME }
        .sumOf { it.amount }

    val currentBalance = totalIncome - totalExpense

    val thisMonthIncome = currentMonthEntries
        .filter { it.type == EntryType.INCOME }
        .sumOf { it.amount }

    val thisMonthExpense = currentMonthEntries
        .filter { it.type == EntryType.OUTCOME }
        .sumOf { it.amount }


    // 3. Spending Overview Calculation (All Outcome Categories for the CURRENT MONTH)
    val currentMonthOutcomeEntries = currentMonthEntries
        .filter { it.type == EntryType.OUTCOME }

    val totalSpending = currentMonthOutcomeEntries.sumOf { it.amount }.toFloat()

    val spendingOverviewData = currentMonthOutcomeEntries
        .filter { it.amount > 0 }
        .groupBy { it.category }
        .map { (category, entries) ->
            val categoryAmount = entries.sumOf { it.amount }
            // Calculate percentage based on current month's total spending
            val percentage = if (totalSpending > 0) (categoryAmount.toFloat() / totalSpending) * 100f else 0f

            SpendingCategory(
                name = category,
                amount = categoryAmount,
                percentage = percentage,
                // Assign a color using the centralized map, falling back to the outline color
                color = CategoryColorMap.getOrDefault(category, MaterialTheme.colorScheme.outline)
            )
        }
        .sortedByDescending { it.amount }
        .take(6) // Take Top 6 for better visibility in the chart and legend

    // 4. Recent Transactions Calculation (Today only)
    val todayTransactions = BudgetEntries
        .filter { it.date == todayDate }
        .sortedByDescending { it.date }
    // --- END: DYNAMIC DATA CALCULATION ---

    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Mooney",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 0.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 28.dp,
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
                        color = MaterialTheme.colorScheme.tertiary // Highlight color

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
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Expense Card
                        StatisticsCard(
                            modifier = Modifier.weight(1f),
                            title = "Expense",
                            amount = formatRupiah(thisMonthExpense),
                            color = MaterialTheme.colorScheme.error
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
                                text = "Belum ada transaksi hari ini.",
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
                                    category = entry.category,
                                    totalAmount = formatRupiah(entry.amount),
                                    // Item represents the note/description
                                    items = listOf(TransactionDetailItem(entry.note, formatRupiah(entry.amount), entry.type.toString()))
                                )

                                // ⭐ CHANGED: Using the new non-expandable component
                                TransactionListItem(
                                    transaction = displayTransaction
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

// Data class for Spending Overview
data class SpendingCategory(
    val name: String,
    val amount: Long,
    val percentage: Float,
    val color: Color
)

// COMPOSABLE: DonutChart (No changes needed here)
@Composable
fun DonutChart(
    data: Map<String, SpendingCategory>,
    totalAmount: Long,
    modifier: Modifier = Modifier
) {
    val entries = data.values.toList()
    val totalFloat = totalAmount.toFloat()

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 16.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val topLeft = Offset(
                (size.width - diameter) / 2f,
                (size.height - diameter) / 2f
            )
            val size = Size(diameter, diameter)

            var startAngle = -90f // Start from the top

            // 1. Draw the segments
            entries.forEach { category ->
                val sweepAngle = if (totalFloat > 0) (category.amount.toFloat() / totalFloat) * 360f else 0f
                if (sweepAngle > 0f) {
                    drawArc(
                        color = category.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = topLeft,
                        size = size,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )
                    startAngle += sweepAngle
                }
            }

            // 2. Draw a small separator (optional, for visual distinction if colors blend)
            startAngle = -90f
            entries.forEach { category ->
                val sweepAngle = if (totalFloat > 0) (category.amount.toFloat() / totalFloat) * 360f else 0f
                if (sweepAngle > 0f) {
                    // Draw a separator at the start of the next arc
                    val endAngle = startAngle + sweepAngle
                    val radius = diameter / 2f + strokeWidth / 2f
                    val center = Offset(this.size.width / 2f, this.size.height / 2f)

                    // Draw a small dot at the separation point
                    drawCircle(
                        color = Color.Black, // Background color for separation
                        radius = 3.dp.toPx(),
                        center = center + Offset(
                            (radius * kotlin.math.cos(Math.toRadians(endAngle.toDouble()))).toFloat(),
                            (radius * kotlin.math.sin(Math.toRadians(endAngle.toDouble()))).toFloat()
                        )
                    )
                    startAngle += sweepAngle
                }
            }
        }

        // Center Text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formatRupiah(totalAmount),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Total Spend",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ⭐ UPDATED COMPOSABLE: CategoryLegendItem
@Composable
fun CategoryLegendItem(
    title: String,
    percentage: Float, // Only receiving percentage now
    color: Color
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 9.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = title, // Only category title
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            // Show percentage only, formatted to one decimal place
            text = "${"%.0f".format(percentage)}%",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun BalanceCard(
    modifier: Modifier = Modifier,
    amount: String,
    color: Color
) {
    Card(
        modifier = modifier
            .height(100.dp), // Slightly taller than StatisticsCard
        colors = CardDefaults.cardColors(containerColor = color.copy()),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Current Balance",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiary,
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = amount,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onTertiary,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun StatisticsCard(
    modifier: Modifier = Modifier,
    title: String,
    amount: String,
    color: Color // Keep the color for the dot
) {
    Card(
        modifier = modifier
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround, // Change alignment to Start
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = amount,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

data class TransactionData(
    val category: String,
    val totalAmount: String,
    val items: List<TransactionDetailItem>
)

data class TransactionDetailItem(
    val name: String,
    val amount: String,
    val description: String
)

// ⭐ NEW COMPOSABLE: Non-Expandable Transaction List Item with Options Menu
@Composable
fun TransactionListItem(
    transaction: TransactionData
) {
    // State to control the visibility of the dropdown menu
    var showMenu by remember { mutableStateOf(false) }

    // We assume the transaction data is primarily from the first item for display
    val item = transaction.items.firstOrNull()
    val note = item?.name ?: "No description"
    val isIncome = item?.description == EntryType.INCOME.toString()

    // The main list item row
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Icon, Category, and Note/Description
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon Placeholder
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
            // Category (Household) and Note (Weekly cleaning service tip)
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = transaction.category, // e.g., Household
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = note, // e.g., Weekly cleaning service tip
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Right side: Amount and Options Icon
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = transaction.totalAmount, // e.g., Rp70.000
                style = MaterialTheme.typography.bodyMedium,
                color = if (isIncome) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.SemiBold
            )

            // Options Icon (MoreVert) and Dropdown Menu
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options menu",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            // TODO: Implement Edit logic here (e.g., navigate to edit screen)
                            showMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            // TODO: Implement Delete logic here (e.g., show confirmation dialog)
                            showMenu = false
                        }
                    )
                }
            }
        }
    }
}


// ⭐ REMOVED/REPLACED: The old ExpandableTransactionCard is no longer used.
// It is good practice to remove unused code to clean up the file. 

// The rest of the composables are unchanged.
// BalanceCard, StatisticsCard, CategoryLegendItem, DonutChart, etc.

// ... (Rest of the unchanged code)