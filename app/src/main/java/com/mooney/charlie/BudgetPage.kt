@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("DEPRECATION")

package com.mooney.charlie

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mooney.charlie.BudgetViewModel
import com.mooney.charlie.ui.theme.CardBackgroundDark
import com.mooney.charlie.ui.theme.CardBackgroundLight

@Composable
fun BudgetPage(
    navController: NavHostController,
    viewModel: BudgetViewModel = viewModel()
) {
    var showEditDialog by remember { mutableStateOf(false) }

    val isDark = isSystemInDarkTheme()
    val customCardColor = if (isDark) CardBackgroundDark else CardBackgroundLight

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Monthly Budget",
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
        Column(
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp) // Add bottom padding for navigation bar
            ) {

                // ITEM 1: Budget Summary Card
                item {
                    BudgetSummaryCard(
                        monthlyBudget = viewModel.monthlyBudget,
                        remainingBudget = viewModel.remainingBudget,
                        totalOutcomeThisMonth = viewModel.totalOutcomeThisMonth,
                        onEditClicked = { showEditDialog = true }
                    )
                }

                // ITEM 2: GRAPHIC TREN & DAILY STATISTICS
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = customCardColor
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {

                            // TITLE
                            Text(
                                "Spending Trend",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            // LINE CHART
                            LineChartView(
                                data = viewModel.dailySpendTrend.map { it.cumulativeSpend.toFloat() },
                                budgetLimit = viewModel.monthlyBudget.toFloat(),
                                totalDaysInMonth = viewModel.totalDaysInMonth
                            )

                            // INTEGRASI DAILY STATISTICS (Tanpa Judul)
                            Spacer(modifier = Modifier.height(24.dp))
                            DailyStatBlock(
                                dailyAverageSpend = viewModel.dailyAverageSpend,
                                recommendedDailySpend = viewModel.recommendedDailySpend,
                                showTitle = false,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        BudgetEditDialog(
            currentBudget = viewModel.monthlyBudget,
            onConfirm = { newAmount ->
                viewModel.updateMonthlyBudget(newAmount)
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }
}
