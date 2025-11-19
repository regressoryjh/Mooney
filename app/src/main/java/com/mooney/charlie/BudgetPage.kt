package com.mooney.charlie

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController

@Composable
fun BudgetPage(navController: NavHostController) {
//    val viewModel: HomeViewModel = viewModel()
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            // Header
            Text(
                text = "Budget Page",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(
                    vertical = 8.dp,
                    horizontal = 8.dp
                )
            ) {
//                items(viewModel.state.value) { restaurant ->
//                    RestaurantItem(
//                        restaurant,
//                        onFavoriteClick = { id, oldValue ->
//                            viewModel.toggleFavorite(id, oldValue) },
//                        onItemClick = { id -> onItemClick(id) })
//                }
            }
        }
    }
}