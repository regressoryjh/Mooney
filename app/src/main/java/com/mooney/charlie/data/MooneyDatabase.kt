package com.mooney.charlie.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mooney.charlie.EntryType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Required to convert the EntryType enum to a storable format (String)
class Converters {
    @TypeConverter
    fun fromEntryType(value: EntryType): String = value.name

    @TypeConverter
    fun toEntryType(value: String): EntryType = EntryType.valueOf(value)
}

@Database(
    entities = [Entry::class, Budget::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun EntryDao(): EntryDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 💡 Callback to populate the database on creation
        private val databaseCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // 📝 Insert the default budget of 0 when the DB is first created
                CoroutineScope(Dispatchers.IO).launch {
                    INSTANCE?.let { database ->
                        // The Budget entity has a fixed ID=1
                        database.budgetDao().insertOrUpdate(Budget(amount = 0L))
                    }
                }
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mooney_database"
                )
                    .fallbackToDestructiveMigration()
                    // Add the callback here
                    .addCallback(databaseCallback)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}