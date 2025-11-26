package com.mooney.charlie.data

//import android.content.Context
//import androidx.room.*
//
//@Database(entities = [Budget::class], version = 1, exportSchema = false)
//abstract class BudgetDatabase : RoomDatabase() {
//
//    abstract fun budgetDao(): BudgetDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: BudgetDatabase? = null
//
//        fun getDatabase(context: Context): BudgetDatabase {
//            // Jika INSTANCE sudah ada, kembalikan INSTANCE
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    BudgetDatabase::class.java,
//                    "budget_database"
//                ).build()
//                INSTANCE = instance
//                // Kembalikan instance yang baru dibuat
//                instance
//            }
//        }
//    }
//}