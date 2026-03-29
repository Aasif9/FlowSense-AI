package com.asif.flowsenseai.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asif.flowsenseai.domain.model.Expense
import com.asif.flowsenseai.domain.repository.ExpenseRepository
import com.asif.flowsenseai.ui.model.TransactionDetailEvent
import com.asif.flowsenseai.ui.model.TransactionDetailUiState
import com.asif.flowsenseai.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val repository: ExpenseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Transaction ID passed via navigation argument
    private val transactionId: Long = savedStateHandle.get<Long>("transactionId") ?: 0L

    private val _uiState = MutableStateFlow(TransactionDetailUiState(isLoading = true))
    val uiState: StateFlow<TransactionDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TransactionDetailEvent>()
    val events = _events.asSharedFlow()

    // Keep reference for delete operation
    private var currentExpense: Expense? = null

    init {
        println("DEBUG: TransactionDetailViewModel - Initialized with transactionId: $transactionId")
        loadTransaction()
    }

    private fun loadTransaction() {
        viewModelScope.launch {
            try {
                println("DEBUG: TransactionDetailViewModel - Loading transactions...")
                repository.getAllExpenses().collect { expenses ->
                    println("DEBUG: TransactionDetailViewModel - Found ${expenses.size} expenses")
                    val expense = expenses.find { it.id == transactionId }
                    if (expense != null) {
                        println("DEBUG: TransactionDetailViewModel - Found expense: ${expense.merchant}")
                        currentExpense = expense
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                id = expense.id,
                                merchantName = expense.merchant,
                                amount = expense.amount,
                                isDebit = expense.type == "debit",
                                category = expense.category,
                                dateDisplay = DateUtils.formatDateTime(expense.date),
                                paymentMethod = "UPI",
                                notes = "",
                                isAutoTracked = true,
                                hasAttachment = false
                            )
                        }
                    } else {
                        println("DEBUG: TransactionDetailViewModel - Transaction not found with ID: $transactionId")
                        _uiState.update { it.copy(isLoading = false, error = "Transaction not found") }
                    }
                }
            } catch (e: Exception) {
                println("DEBUG: TransactionDetailViewModel - Error loading transaction: ${e.message}")
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onEditClicked() {
        viewModelScope.launch {
            _events.emit(TransactionDetailEvent.NavigateToEdit(transactionId))
        }
    }

    fun onDeleteClicked() {
        viewModelScope.launch {
            try {
                currentExpense?.let { expense ->
                    repository.deleteExpense(expense)
                    _events.emit(TransactionDetailEvent.DeleteSuccess)
                }
            } catch (e: Exception) {
                _events.emit(TransactionDetailEvent.DeleteError(e.message ?: "Delete failed"))
            }
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _events.emit(TransactionDetailEvent.NavigateBack)
        }
    }
}
