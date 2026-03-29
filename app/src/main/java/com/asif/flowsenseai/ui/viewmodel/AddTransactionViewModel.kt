package com.asif.flowsenseai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asif.flowsenseai.domain.model.Expense
import com.asif.flowsenseai.domain.repository.ExpenseRepository
import com.asif.flowsenseai.ui.model.AddTransactionEvent
import com.asif.flowsenseai.ui.model.AddTransactionUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddTransactionEvent>()
    val events = _events.asSharedFlow()

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    init {
        // Set today's date as default
        _uiState.update { it.copy(selectedDate = dateFormat.format(Date())) }
    }

    // ── Public events from UI ─────────────────────────────────────────────────

    fun onTypeToggled(isExpense: Boolean) {
        println("DEBUG: AddTransactionViewModel - onTypeToggled called with isExpense: $isExpense")
        _uiState.update { 
            val newState = it.copy(isExpense = isExpense)
            println("DEBUG: AddTransactionViewModel - Updated isExpense to: ${newState.isExpense}")
            newState
        }
    }

    fun onAmountChanged(amount: String) {
        // Only allow decimal numbers with proper formatting
        val formatted = amount
            .replace(",", "")
            .replace(" ", "")
            .let { if (it.startsWith(".")) "0$it" else it }
            .let { if (it.contains(".")) {
                val parts = it.split(".")
                if (parts.size > 2) parts[0] + "." + parts[1] else it
            } else it }
        
        _uiState.update { it.copy(amountText = formatted) }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onDateChanged(date: String) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    fun onNotesChanged(notes: String) {
        _uiState.update { 
            it.copy(
                notes = notes,
                draftSaved = notes.isNotBlank() // Show draft indicator when notes are added
            ) 
        }
    }

    fun onAttachReceipt() {
        _uiState.update { it.copy(hasAttachment = !it.hasAttachment) }
    }

    fun onSaveClicked() {
        val state = _uiState.value
        if (!state.canSave) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSaving = true) }

                // Parse amount
                val amount = state.amountText.toDoubleOrNull()
                if (amount == null || amount <= 0) {
                    _events.emit(AddTransactionEvent.ShowError("Please enter a valid amount"))
                    _uiState.update { it.copy(isSaving = false) }
                    return@launch
                }

                // Parse date
                val date = try {
                    dateFormat.parse(state.selectedDate)?.time ?: Date().time
                } catch (e: Exception) {
                    Date().time
                }

                // Create expense object
                val expense = Expense(
                    id = 0L, // Let repository assign ID
                    merchant = state.selectedCategory, // Use category as merchant for manual entries
                    amount = amount,
                    type = if (state.isExpense) "debit" else "credit",
                    category = state.selectedCategory,
                    date = date,
                    notes = state.notes.takeIf { it.isNotBlank() }
                )

                // Save to repository
                //repository.insertExpense(expense)
                repository.addExpense(expense)
                // Emit success event
                _events.emit(AddTransactionEvent.SaveSuccess)
                
            } catch (e: Exception) {
                _events.emit(AddTransactionEvent.ShowError("Failed to save transaction: ${e.message}"))
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun onNavigateBack() {
        viewModelScope.launch {
            _events.emit(AddTransactionEvent.NavigateBack)
        }
    }
}
