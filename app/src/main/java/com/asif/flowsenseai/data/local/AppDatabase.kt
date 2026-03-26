package com.asif.flowsenseai.data.local


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


/**
 * Room Database class for the FlowSense AI app.
 * This is the main database that holds all our expense data.
 */
@Database(
    entities = [ExpenseEntity::class], // List of entities in the database
    version = 1, // Database version - increment when schema changes
    exportSchema = false // Set to true if you want to export schema
)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Abstract method to get the Expense DAO.
     * Room will automatically implement this for us.
     */
    abstract fun expenseDao(): ExpenseDao
    
    companion object {
        /**
         * Singleton instance of the database.
         * Volatile annotation ensures that writes to this field are immediately made visible to other threads.
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Get the database instance.
         * Uses double-checked locking pattern to ensure thread safety.
         */
        fun getDatabase(context: Context): AppDatabase {
            // If instance is not null, return it
            return INSTANCE ?: synchronized(this) {
                // If instance is still null, create a new database
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flowsense_database" // Name of the database file
                )
                .fallbackToDestructiveMigration() // This will recreate the database if schema changes
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
