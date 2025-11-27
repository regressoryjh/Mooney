package com.mooney.charlie

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mooney.charlie.data.*
import com.mooney.charlie.ui.theme.PrimaryLight
import com.mooney.charlie.ui.theme.OnPrimaryLight

// Define the items that will appear in the Bottom Navigation Bar
enum class BottomNavDestination(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    HISTORY(
        route = Destinations.HISTORY,
        icon = Icons.Filled.History,
        label = "History"
    ),
    HOME(
        route = Destinations.HOME,
        icon = Icons.Filled.Home,
        label = "Home"
    ),
    BUDGET(
        route = Destinations.BUDGET,
        icon = Icons.Filled.AccountBalanceWallet,
        label = "Budget"
    ),
    NEW(
        route = Destinations.NEW,
        icon = Icons.Filled.AddCircleOutline,
        label = "New"
    )
}



@Composable
fun NavigationBar(repository: AppRepository) {
    val navController = rememberNavController()
    // Set the start destination using the enum
    val startDestination = BottomNavDestination.HOME

    // We track the index of the currently selected destination
    var selectedDestinationIndex by rememberSaveable {
        mutableIntStateOf(BottomNavDestination.entries.indexOf(startDestination))
    }

    // ⭐ LANGKAH 2A: Dapatkan entry back stack saat ini sebagai State
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // ⭐ 1. Tentukan apakah Bottom Bar harus ditampilkan
    val showBottomBar = currentRoute in listOf(
        Destinations.HOME,
        Destinations.HISTORY,
        Destinations.BUDGET
        // Destinations.NEW TIDAK ADA di sini, jadi Bottom Bar akan disembunyikan di halaman ini
    )

    // ⭐ LANGKAH 2B: Gunakan LaunchedEffect untuk memperbarui index saat rute berubah
    LaunchedEffect(currentRoute) {
        // Cari BottomNavDestination yang rutenya cocok dengan rute saat ini
        val destination = BottomNavDestination.entries.find { it.route == currentRoute }
        destination?.let {
            // Jika ditemukan, perbarui index sorotan
            selectedDestinationIndex = BottomNavDestination.entries.indexOf(it)
        }
    }

    // Adaptive colors for Navigation Bar Items to match Balance Card
    val isDark = isSystemInDarkTheme()
    val activeIndicatorColor = if (isDark) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primary
    val activeIconColor = if (isDark) Color.White else MaterialTheme.colorScheme.onPrimary

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                // These animations should roughly match typical navigation speeds (e.g., 300ms)
                enter = slideInVertically(animationSpec = tween(200)) { it } + fadeIn(animationSpec = tween(200)),
                exit = slideOutVertically(animationSpec = tween(200)) { it } + fadeOut(animationSpec = tween(200))
            ){
                NavigationBar(
                    modifier = Modifier.shadow(elevation = 8.dp),
                    // ⭐ CHANGED: Use MaterialTheme.colorScheme.background to match screen background
                    containerColor = MaterialTheme.colorScheme.background, 
                    windowInsets = NavigationBarDefaults.windowInsets
                ) {
                    BottomNavDestination.entries.forEachIndexed { index, destination ->
                        NavigationBarItem(
                            // Gunakan status yang DIPERBARUI oleh LaunchedEffect
                            selected = selectedDestinationIndex == index,
                            onClick = {
                                // Special handling for NEW to avoid clearing back stack
                                if (destination.route == Destinations.NEW) {
                                    navController.navigate(destination.route)
                                } else if (selectedDestinationIndex != index) {
                                    // Standard bottom nav behavior
                                    navController.navigate(route = destination.route) {
                                        // Logika popUpTo dan launchSingleTop
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    destination.icon,
                                    contentDescription = destination.label // Use label as content description
                                )
                            },
                            label = { Text(destination.label) },
                            colors = NavigationBarItemDefaults.colors(
                                // Adaptive colors matching Balance Card
                                indicatorColor = activeIndicatorColor,
                                selectedIconColor = activeIconColor,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }
    ) { contentPadding ->
        // Call your existing NavGraph here
        NavGraph(
            navController = navController,
            repository = repository, // <--- PASS REPOSITORY
            modifier = Modifier.padding(top = contentPadding.calculateTopPadding()) // Only apply top padding to let content go behind bottom bar
        )
    }
}
