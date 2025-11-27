package com.mooney.charlie

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.*
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.mooney.charlie.data.*
import kotlinx.coroutines.flow.firstOrNull

object Destinations {
    const val HOME = "home"
    const val HISTORY = "history"
    const val BUDGET = "budget"
    const val NEW = "add new"
    // Defines argument key
    const val ENTRY_ID_KEY = "entryId"
    // Route with argument: "add new?entryId={entryId}"
    const val NEW_WITH_ARG = "$NEW?$ENTRY_ID_KEY={$ENTRY_ID_KEY}"
}

// Define a simple factory wrapper to pass the repository dependency
class MooneyViewModelFactory(
    private val repository: AppRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> HomeViewModel(repository) as T
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> HistoryViewModel(repository) as T
            modelClass.isAssignableFrom(BudgetViewModel::class.java) -> BudgetViewModel(repository) as T
            modelClass.isAssignableFrom(NewEntryViewModel::class.java) -> NewEntryViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    repository: AppRepository, // <--- ADD REPOSITORY PARAMETER
    modifier: Modifier
) {
    // Create a factory instance to be used for all ViewModels
    val factory = remember { MooneyViewModelFactory(repository) }

    NavHost(
        navController = navController,
        startDestination = Destinations.HOME,
        modifier = modifier // Apply the modifier passed from Scaffold
    ) {
        // 1. HomePage route
        composable (route = Destinations.HOME) {
            val homeViewModel: HomeViewModel = viewModel(factory = factory)
            HomePage (navController, viewModel = homeViewModel)
        }

        // 2. HistoryPage route
        composable(route = Destinations.HISTORY) {
            val historyViewModel: HistoryViewModel = viewModel(factory = factory)
            HistoryPage (navController, viewModel = historyViewModel)
        }

        // 3. BudgetPage route
        composable (route = Destinations.BUDGET) {
            val budgetViewModel: BudgetViewModel = viewModel(factory = factory)
            BudgetPage (navController, viewModel = budgetViewModel)
        }

        // 4. NewEntry route (Modified to accept optional argument)
        composable (
            route = Destinations.NEW_WITH_ARG,
            arguments = listOf(
                navArgument(Destinations.ENTRY_ID_KEY) {
                    type = NavType.LongType
                    defaultValue = -1L // Default to -1 if not provided (Creating new)
                }
            ),
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
        ) { backStackEntry ->
            val newEntryViewModel: NewEntryViewModel = viewModel(factory = factory)

            // Retrieve argument
            val entryId = backStackEntry.arguments?.getLong(Destinations.ENTRY_ID_KEY) ?: -1L

            // State to hold the fetched entry
            var initialEntry by remember { mutableStateOf<Entry?>(null) }

            // If we have a valid ID, fetch the entry
            LaunchedEffect(entryId) {
                if (entryId != -1L) {
                    val entry = repository.getEntryById(entryId.toInt()).firstOrNull()
                    initialEntry = entry
                }
            }

            // Pass to NewEntry screen
            // We only show the screen once we've determined if we are editing (and fetched data) or creating
            // Or we can pass null initially and let NewEntry handle it
            NewEntry (
                navController,
                viewModel = newEntryViewModel,
                initialEntry = initialEntry
            )
        }
    }
}
