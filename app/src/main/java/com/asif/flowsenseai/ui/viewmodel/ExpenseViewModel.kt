package com.asif.flowsenseai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asif.flowsenseai.domain.model.Expense
import com.asif.flowsenseai.domain.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses.asStateFlow()

    private val _totalSpent = MutableStateFlow(0.0)
    val totalSpent: StateFlow<Double> = _totalSpent.asStateFlow()

    init {
        loadExpenses()
        loadTotalSpent()
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            repository.getAllExpenses().collect { expenseList ->
                _expenses.value = expenseList
            }
        }
    }

    private fun loadTotalSpent() {
        viewModelScope.launch {
            repository.getTotalSpent().collect { total ->
                _totalSpent.value = total
            }
        }
    }

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

    fun addExpense(expense: Expense) {
        viewModelScope.launch { repository.addExpense(expense) }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch { repository.deleteExpense(expense) }
    }

    fun getFormattedAmount(amount: Double): String = "₹%.2f".format(amount)
}
