@file:OptIn(ExperimentalMaterial3Api::class)
@file:Suppress("DEPRECATION")

package com.mooney.charlie

import androidx.compose.foundation.BorderStroke
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

@Composable
fun BudgetPage(
    navController: NavHostController,
    viewModel: BudgetViewModel = viewModel()
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
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
                    .padding(top = 20.dp, start = 16.dp, end = 16.dp, bottom = 0.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
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

                // ITEM 2: CARD: GRAFIK TREN & DAILY STATISTICS
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f)),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            // CARD TITLE
                            Text(
                                "Spending Trend",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            // LINE CHART
                            LineChartView(
                                data = viewModel.dailySpendTrend.map { it.cumulativeSpend.toFloat() },
                                budgetLimit = viewModel.monthlyBudget.toFloat(),
                                totalDaysInMonth = viewModel.totalDaysInMonth // Tambahkan total hari
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