package com.asif.flowsenseai.domain.repository

import com.asif.flowsenseai.data.local.ExpenseDao
import com.asif.flowsenseai.data.notification.ParsedUpiTransaction
import com.asif.flowsenseai.data.notification.toExpense
import com.asif.flowsenseai.domain.model.Expense
import com.asif.flowsenseai.domain.model.toExpense
import com.asif.flowsenseai.domain.model.toExpenseEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository interface for expense operations.
 * This defines the contract for what our repository can do.
 * Using interface helps with testing and follows dependency inversion principle.
 */
interface ExpenseRepository {
    
    /**
     * Get all expenses as a Flow.
     * Flow automatically updates when data changes in the database.
     */
    fun getAllExpenses(): Flow<List<Expense>>
    
    /**
     * Add a new expense to the database.
     * @param expense The expense to add
     */
    suspend fun addExpense(expense: Expense)
    
    /**
     * Delete an expense from the database.
     * @param expense The expense to delete
     */
    suspend fun deleteExpense(expense: Expense)
    
    /**
     * Get total amount spent across all expenses.
     */
    fun getTotalSpent(): Flow<Double>
    
    /**
     * Add a dummy expense for testing purposes.
     * This will be used by our FAB button.
     */
    suspend fun addDummyExpense()
    
    /**
     * Insert expense from parsed UPI notification.
     * This will be used to save automatically parsed transactions.
     */
    suspend fun insertFromNotification(parsed: ParsedUpiTransaction)
}

/**
 * Implementation of ExpenseRepository.
 * This class talks to the database through the DAO.
 * We're using constructor injection for the DAO parameter.
 */
class ExpenseRepositoryImpl(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {
    
    override fun getAllExpenses(): Flow<List<Expense>> {
        // Get expenses from DAO and convert them to domain models
        return expenseDao.getAllExpenses().map { entities ->
            entities.map { it.toExpense() }
        }
    }
    
    override suspend fun addExpense(expense: Expense) {
        // Convert domain model to entity and save to database
        expenseDao.insert(expense.toExpenseEntity())
    }
    
    override suspend fun deleteExpense(expense: Expense) {
        // Convert domain model to entity and delete from database
        expenseDao.delete(expense.toExpenseEntity())
    }
    
    override fun getTotalSpent(): Flow<Double> {
        // Directly return the total spent from DAO
        return expenseDao.getTotalSpent()
    }
    
    override suspend fun addDummyExpense() {
        // Create a dummy expense (₹250 to Zomato)
        val dummyExpense = Expense(
            amount = 250.0,
            merchant = "Zomato",
            category = "Food",
            date = System.currentTimeMillis(),
            type = "debit"
        )
        addExpense(dummyExpense)
    }
    
    override suspend fun insertFromNotification(parsed: ParsedUpiTransaction) {
        // Convert ParsedUpiTransaction to Expense and save to database
        val expense = parsed.toExpense()
        addExpense(expense)
    }
}
