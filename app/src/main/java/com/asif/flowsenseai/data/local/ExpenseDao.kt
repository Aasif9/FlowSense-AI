package com.asif.flowsenseai.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for ExpenseEntity.
 * This interface defines all the database operations for expenses.
 * Room will automatically generate the implementation for us.
 */
@Dao
interface ExpenseDao {
    
    /**
     * Insert a new expense into the database.
     * @param expense The expense to insert
     * @return The row ID of the inserted expense
     */
    @Insert
    suspend fun insert(expense: ExpenseEntity): Long
    
    /**
     * Get all expenses from the database as a Flow.
     * Flow will automatically emit new values whenever the data changes.
     * @return Flow of List<ExpenseEntity> - all expenses in the database
     */
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>
    
    /**
     * Delete an expense from the database.
     * @param expense The expense to delete
     * @return Number of rows affected (should be 1 if successful)
     */
    @Delete
    suspend fun delete(expense: ExpenseEntity): Int
    
    /**
     * Get the total amount spent across all expenses.
     * @return Flow<Double> - total amount spent
     */
    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE type = 'debit'")
    fun getTotalSpent(): Flow<Double>
    
    /**
     * Get expenses by category.
     * @param category The category to filter by
     * @return Flow<List<ExpenseEntity>> - expenses in the specified category
     */
    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: String): Flow<List<ExpenseEntity>>
    
    /**
     * Delete all expenses from the database.
     * Useful for testing or resetting the app.
     * @return Number of rows deleted
     */
    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses(): Int
}
