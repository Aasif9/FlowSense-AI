package com.asif.flowsenseai.ui.model

/**
 * UI state for Add Transaction screen.
 * Follows single source of truth pattern.
 */
data class AddTransactionUiState(
    val isExpense: Boolean = true,
    val amountText: String = "",
    val selectedCategory: String = "",
    val selectedDate: String = "",
    val notes: String = "",
    val hasAttachment: Boolean = false,
    val draftSaved: Boolean = false,
    val isSaving: Boolean = false
) {
    val canSave: Boolean
        get() = amountText.isNotBlank() && 
                selectedCategory.isNotBlank() && 
                selectedDate.isNotBlank() &&
                !isSaving
}

/**
 * One-shot events from AddTransactionViewModel.
 */
sealed class AddTransactionEvent {
    data object SaveSuccess : AddTransactionEvent()
    data object NavigateBack : AddTransactionEvent()
    data class ShowError(val message: String) : AddTransactionEvent()
}

/**
 * Category option for dropdown selection.
 */
data class CategoryOption(
    val name: String,
    val iconResId: Int
)
