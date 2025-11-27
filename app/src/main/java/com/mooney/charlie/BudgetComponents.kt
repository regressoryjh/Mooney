@file:Suppress("DEPRECATION")

package com.mooney.charlie

import android.R.attr.entries
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.absoluteValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.material3.MaterialTheme
import com.mooney.charlie.ui.theme.ExpenseRed
import com.mooney.charlie.ui.theme.PrimaryLight

@Composable
fun BudgetEditDialog(
    currentBudget: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var amountText by rememberSaveable { mutableStateOf(currentBudget.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Monthly Budget") },
        text = {
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { char -> char.isDigit() } },
                label = { Text("Budget Amount (Rp)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val newAmount = amountText.toLongOrNull() ?: 0L
                    onConfirm(newAmount)
                },
                enabled = amountText.isNotEmpty() && amountText.toLongOrNull() != null
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun BudgetSummaryCard(
    monthlyBudget: Long,
    remainingBudget: Long,
    totalOutcomeThisMonth: Long,
    onEditClicked: () -> Unit
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
    val formattedMonthly = numberFormat.format(monthlyBudget)
    // FIX: Don't use absolute value here to keep the sign, but handle the sign manually
    val formattedRemaining = numberFormat.format(remainingBudget.absoluteValue)
    val sign = if (remainingBudget < 0) "-" else ""

    val percentSpent = if (monthlyBudget > 0) (totalOutcomeThisMonth.toFloat() / monthlyBudget.toFloat()) * 100 else 0f

    val isDark = isSystemInDarkTheme()
    // Use primary container color like BalanceCard
    val cardColor = if (isDark) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary
    
    // ⭐ CHANGED: Use White text in Dark Mode for better contrast against Dark Green, consistent with BalanceCard
    val contentColor = if (isDark) Color.White else MaterialTheme.colorScheme.onPrimary
    
    val remainingTextColor = contentColor

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp), // Match BalanceCard radius
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Match BalanceCard elevation
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Remaining Budget",
                        style = MaterialTheme.typography.titleMedium,
                        color = contentColor,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                    Text(
                        text = "$sign Rp $formattedRemaining / Rp $formattedMonthly",
                        style = MaterialTheme.typography.headlineSmall,
                        color = remainingTextColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onEditClicked) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Edit Monthly Budget",
                        tint = contentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { percentSpent.coerceIn(0f, 100f) / 100f },
                color = contentColor, // Use content color for progress so it's visible
                trackColor = contentColor.copy(alpha = 0.3f), // Semitransparent track
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(Modifier.height(4.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    text = "${percentSpent.toInt()}% Spent",
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun DailyStatBlock(
    dailyAverageSpend: Long,
    recommendedDailySpend: Long,
    modifier: Modifier = Modifier,
    showTitle: Boolean = true // ⭐ Tambahkan parameter ini
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale("id", "ID"))
    val formattedAverage = numberFormat.format(dailyAverageSpend)
    val formattedRecommended = numberFormat.format(recommendedDailySpend)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 0.dp, bottom = 0.dp) // Sesuaikan padding agar tidak terlalu tebal
    ) {
        if (showTitle) {
            Text(
                text = "Daily Statistics",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // KIRI: Daily Average Spend
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Rp $formattedAverage",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Daily Average Spend",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // GARIS PEMISAH
//            Spacer(modifier = Modifier.width(16.dp))
            Divider(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp)
                    .align(Alignment.CenterVertically),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
//            Spacer(modifier = Modifier.width(16.dp))

            // KANAN: Recommended Daily Spend
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Rp $formattedRecommended",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Recommended",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LineChartView(
    data: List<Float>,
    budgetLimit: Float,
    totalDaysInMonth: Int,
    modifier: Modifier = Modifier
) { // Parameter baru

    val entries = data.mapIndexed { index, cumulativeSpend ->
        Entry(index.toFloat(), cumulativeSpend)
    }

    val spentColor = MaterialTheme.colorScheme.primary
    val limitColor = MaterialTheme.colorScheme.tertiary
    val textColor = MaterialTheme.colorScheme.onSurface

    val lightGridColorInt = MaterialTheme.colorScheme.outlineVariant.toArgb()
    val spentColorInt = spentColor.toArgb()
    val limitColorInt = limitColor.toArgb()
    val textColorInt = textColor.toArgb()

    // DataSet 1: Cumulative Spent
    val spentDataSet = LineDataSet(entries, "Spent").apply {
        color = spentColorInt // FIX: menggunakan toArgb
        lineWidth = 2f
        circleRadius = 4f
        setCircleColor(spentColorInt)
        mode = LineDataSet.Mode.CUBIC_BEZIER
        setDrawValues(false)
        setDrawCircles(false)
    }

    // DataSet 2: Budget Limit
    val limitEntries = listOf(
        Entry(0f, budgetLimit),
        Entry(totalDaysInMonth.toFloat() - 1, budgetLimit) // Set batas X hingga akhir bulan
    )
    val limitDataSet = LineDataSet(limitEntries, "Budget Limit").apply {
        color = limitColor.toArgb() // FIX: menggunakan toArgb
        lineWidth = 1f
        enableDashedLine(10f, 10f, 0f)
        setDrawValues(false)
        setDrawCircles(false)
    }

    val lineData = LineData(spentDataSet, limitDataSet)

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                axisRight.isEnabled = false

                // ⭐ FIX 1: Configure Legend Position (Top Right)
                legend.apply {
                    isEnabled = true
                    verticalAlignment = Legend.LegendVerticalAlignment.TOP
                    horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                    orientation = Legend.LegendOrientation.HORIZONTAL
                    setDrawInside(true)
                    // Customize text size/color if needed
                    this.textColor = textColorInt
                }

                // X-AXIS FIXES
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(true)
                xAxis.setDrawAxisLine(true)
                xAxis.axisMinimum = 0f
                xAxis.axisMaximum = totalDaysInMonth.toFloat() - 1 // Max X = Hari terakhir bulan
                xAxis.setAvoidFirstLastClipping(true) // Memastikan label pertama/terakhir tidak terpotong
                xAxis.gridColor = lightGridColorInt
                xAxis.textColor = textColorInt

                // Y-AXIS FIXES
                axisLeft.setDrawGridLines(true)
                axisLeft.gridColor = lightGridColorInt
                axisLeft.axisMinimum = 0f
                axisLeft.axisMaximum = budgetLimit * 1.2f
                axisLeft.setDrawZeroLine(true) // ⭐ Fix 2: Agar sumbu Y memotong di (0,0)
                axisLeft.textColor = textColorInt

                //
                axisLeft.gridColor = lightGridColorInt
                xAxis.gridColor = lightGridColorInt

                // ⭐ Fix 3: Hapus label 0 pada sumbu Y, karena sumbu X sudah memotong di 0
                axisLeft.valueFormatter = object : ValueFormatter() {
                    val numberFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                    override fun getFormattedValue(value: Float): String {
                        if (value == 0f) return ""
                        // Crash mungkin terjadi saat mencoba memformat
                        return numberFormat.format(value.toDouble()).replace("Rp", "")
                    }
                }

                // Set Data dan Draw
                setData(lineData)
                invalidate()
            }
        },
        update = { chart ->
            chart.data = lineData
            chart.xAxis.axisMaximum = totalDaysInMonth.toFloat() - 1
            // Update Y-Axis maximum to reflect new budget
            chart.axisLeft.axisMaximum = budgetLimit * 1.2f
            
            // Update colors for dark/light mode
            chart.legend.textColor = textColorInt
            chart.xAxis.textColor = textColorInt
            chart.axisLeft.textColor = textColorInt
            chart.xAxis.gridColor = lightGridColorInt
            chart.axisLeft.gridColor = lightGridColorInt
            
            chart.notifyDataSetChanged()
            chart.invalidate()
        }
    )
}
