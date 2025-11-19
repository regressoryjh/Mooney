package com.mooney.charlie

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController

// Compose Core
import androidx.compose.runtime.Composable
// State Management
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableIntStateOf
// UI Layouts and Components
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
// Navigation Bar Components (Material 3)
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarDefaults
// Jetpack Compose Navigation
import androidx.navigation.compose.rememberNavController

@Composable
fun HomePage(navController: NavHostController) {
//    val viewModel: HomeViewModel = viewModel()
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            // Header
            Text(
                text = "Home Page",
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

