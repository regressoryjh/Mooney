package com.mooney.charlie

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.mooney.charlie.data.Entry
import com.mooney.charlie.ui.theme.CardBackgroundDark
import com.mooney.charlie.ui.theme.CardBackgroundLight
import com.mooney.charlie.ui.theme.IncomeGreen
import com.mooney.charlie.ui.theme.ExpenseRed

@Composable
fun BalanceCard(
    modifier: Modifier = Modifier,
    amount: String,
    color: Color // We determine color internally for theme coherence
) {
    val isDark = isSystemInDarkTheme()
    // Light Mode: Use Primary (Deep Green)
    // Dark Mode: Use PrimaryContainer (Dark Green) to avoid "too light" Pastel Green
    val cardColor = if (isDark) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary
    
    // ⭐ CHANGED: Use White text in Dark Mode for better contrast against Dark Green
    val contentColor = if (isDark) Color.White else MaterialTheme.colorScheme.onPrimary

    Card(
        modifier = modifier
            .height(100.dp), // Slightly taller than StatisticsCard
        // ⭐ CHANGED: Use Primary (Green) directly as requested for HomePage cards
        colors = CardDefaults.cardColors(containerColor = cardColor), // Revert to using cardColor for adaptive dark mode
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Balance",
                style = MaterialTheme.typography.titleLarge,
                color = contentColor, // Use contentColor for adaptive contrast
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = amount,
                style = MaterialTheme.typography.headlineLarge,
                color = contentColor, // Use contentColor for adaptive contrast
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
        colors = CardDefaults.cardColors(containerColor = color.copy()),
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
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
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

    val isDark = isSystemInDarkTheme()
    val customCardColor = if (isDark) CardBackgroundDark else CardBackgroundLight
    val separatorColor = customCardColor


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

                    // Draw a small line at the separation point (transparent line)
                    // ADJUSTED: Longer line and Thinner line as requested
                    val lineLength = strokeWidth * 2f // Increased length
                    val lineStartRadius = (diameter / 2f) - (strokeWidth * 0.4f) // Adjusted start
                    val lineEndRadius = lineStartRadius + lineLength

                    val angleRad = Math.toRadians(endAngle.toDouble())
                    val lineStart = center + Offset(
                        (lineStartRadius * kotlin.math.cos(angleRad)).toFloat(),
                        (lineStartRadius * kotlin.math.sin(angleRad)).toFloat()
                    )
                    val lineEnd = center + Offset(
                        (lineEndRadius * kotlin.math.cos(angleRad)).toFloat(),
                        (lineEndRadius * kotlin.math.sin(angleRad)).toFloat()
                    )

                    drawLine(
                        color = separatorColor,
                        start = lineStart,
                        end = lineEnd,
                        strokeWidth = 2.dp.toPx(), // Reduced width (thinner)
                        cap = StrokeCap.Butt
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

data class TransactionData(
    val entry: Entry,
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
    transaction: TransactionData,
    onDeleteClick: (Entry) -> Unit,
    onEditClick: (Entry) -> Unit // <--- Added Edit Callback
) {
    // State to control the visibility of the dropdown menu
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

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
            // Category Icon Placeholder - now clickable for menu
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable { showMenu = true } // Trigger menu on click
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    // ⭐ ADDED: Match the background color of the popup to the screen background
                    containerColor = MaterialTheme.colorScheme.background
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            showMenu = false
                            onEditClick(transaction.entry) // <--- Call Edit
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            showMenu = false
                            showDeleteDialog = true
                        }
                    )
                }
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
                color = if (isIncome) IncomeGreen else ExpenseRed, // Using specific green/red colors
                fontWeight = FontWeight.SemiBold
            )
            
            // Removed the options icon button from here as it is now integrated into the leading circle icon
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick(transaction.entry)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
