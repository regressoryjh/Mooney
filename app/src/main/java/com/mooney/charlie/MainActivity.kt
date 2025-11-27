package com.mooney.charlie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mooney.charlie.data.*
import com.mooney.charlie.ui.theme.MooneyCharlieTheme

class MainActivity : ComponentActivity() {

    // 1. Database and Repository Initialization (Lazy initialization is good here)
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { AppRepository(database.EntryDao(), database.budgetDao()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MooneyCharlieTheme {
                // 2. Pass the repository to the Navigation entry point
                NavigationBar(repository = repository) // <--- PASS REPOSITORY
            }
        }
    }
}