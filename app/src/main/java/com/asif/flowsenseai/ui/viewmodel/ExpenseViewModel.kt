package com.asif.flowsenseai.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.asif.flowsenseai.data.local.AppDatabase
import com.asif.flowsenseai.data.local.ExpenseDao
import com.asif.flowsenseai.domain.model.Expense
import com.asif.flowsenseai.domain.repository.ExpenseRepository
import com.asif.flowsenseai.domain.repository.ExpenseRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * AndroidViewModel for managing expense data.
 * Extends AndroidViewModel to get Application context for database access.
 * This ViewModel survives configuration changes and provides data to the UI.
 */
class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    
    // Initialize database and DAO
    private val database: AppDatabase = AppDatabase.getDatabase(application)
    private val expenseDao: ExpenseDao = database.expenseDao()
    
    // Initialize repository with DAO (constructor injection)
    private val repository: ExpenseRepository = ExpenseRepositoryImpl(expenseDao)
    
    // Private MutableStateFlow to hold the list of expenses
    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    
    // Public immutable StateFlow that UI can observe
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()
    
    // Private MutableStateFlow to hold total spent amount
    private val _totalSpent = MutableStateFlow<Double>(0.0)
    
    // Public immutable StateFlow for total spent
    val totalSpent: StateFlow<Double> = _totalSpent.asStateFlow()
    
    init {
        // Load expenses when ViewModel is created
        loadExpenses()
        loadTotalSpent()
    }
    
    /**
     * Load all expenses from the repository.
     * Uses viewModelScope to automatically cancel coroutines when ViewModel is cleared.
     */
    private fun loadExpenses() {
        viewModelScope.launch {
            repository.getAllExpenses().collect { expenseList ->
                _expenses.value = expenseList
            }
        }
    }
    
    /**
     * Load total spent amount from the repository.
     */
    private fun loadTotalSpent() {
        viewModelScope.launch {
            repository.getTotalSpent().collect { total ->
                _totalSpent.value = total
            }
        }
    }
    
    /**
     * Add a dummy expense (₹250 to Zomato - Food category).
     * This will be called when user clicks the FAB button.
     * Creates a realistic expense with current timestamp.
     */
    fun addDummyExpense() {
        viewModelScope.launch {
            val dummyExpense = Expense(
                amount = 250.0,
                merchant = "Zomato",
                category = "Food",
                date = System.currentTimeMillis(),
                type = "debit"
            )
            repository.addExpense(dummyExpense)
        }
    }
    
    /**
     * Add a new expense to the database.
     * @param expense The expense to add
     */
    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            repository.addExpense(expense)
        }
    }
    
    /**
     * Delete an expense from the database.
     * @param expense The expense to delete
     */
    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }
    
    /**
     * Get formatted amount string with currency symbol.
     * @param amount The amount to format
     * @return Formatted amount string (e.g., "₹250.00")
     */
    fun getFormattedAmount(amount: Double): String {
        return String.format("₹%.2f", amount)
    }
}
