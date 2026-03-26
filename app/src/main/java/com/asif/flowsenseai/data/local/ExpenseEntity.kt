package com.asif.flowsenseai.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for storing expense information in the database.
 * This represents a single expense record with all its details.
 */
@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Auto-generated primary key
    
    val amount: Double, // Amount spent (e.g., 250.50)
    
    val merchant: String, // Where the expense was made (e.g., "Zomato", "Amazon")
    
    val category: String, // Category of expense (e.g., "Food", "Shopping", "Transport")
    
    val date: Long = System.currentTimeMillis(), // Timestamp of when expense was made
    
    val type: String = "debit" // Type of transaction (default is "debit")
)
