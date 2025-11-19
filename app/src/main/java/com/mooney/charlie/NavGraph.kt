package com.mooney.charlie

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.*
import androidx.compose.animation.*
import androidx.compose.animation.core.tween

object Destinations {
    const val HOME = "home"
    const val HISTORY = "history"
    const val BUDGET = "budget"
    const val NEW = "add new"
}

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = Destinations.HOME
    ) {
        // 1. HomePage route
        composable (
            route = Destinations.HOME
        ) {
            HomePage (navController)
        }

        // 2. HistoryPage route
        composable(
            route = Destinations.HISTORY,
        ) {
            HistoryPage (navController)
        }
        // 3. BudgetPage route
        composable (
            route = Destinations.BUDGET
        ) {
            BudgetPage (navController)
        }

        // 4. NewEntry route
        composable (
            route = Destinations.NEW,
            // Animasi untuk MASUK ke halaman ini (default: slide dari kanan)
            enterTransition = {
                slideInVertically(
                    animationSpec = tween(500),
                    initialOffsetY = { it }
                ) // + fadeIn(animationSpec = tween(500))
            },
            // Animasi untuk KELUAR dari halaman ini (kita paksa slide ke kanan)
            exitTransition = {
                slideOutVertically(
                    animationSpec = tween(500),
                    targetOffsetY = { it }
                ) // + fadeOut(animationSpec = tween(500))
            },
            // Animasi untuk POP OUT (saat popBackStack dipanggil)
            popExitTransition = {
                slideOutVertically(
                    animationSpec = tween(500),
                    targetOffsetY = { it }
                ) // + fadeOut(animationSpec = tween(500))
            }
        ) {
            NewEntry (navController)
        }
    }
}