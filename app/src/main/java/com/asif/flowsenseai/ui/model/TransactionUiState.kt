package com.asif.flowsenseai.ui.model

/**
 * UI state for the Transaction screen.
 * Sealed class cleanly handles loading/error/success states.
 */
sealed class TransactionScreenState {
    data object Loading : TransactionScreenState()
    data class Error(val message: String) : TransactionScreenState()
    data class Success(
        val selectedMonth: String,
        val selectedFilter: TransactionFilter,
        val groupedTransactions: List<TransactionGroup>
    ) : TransactionScreenState()
}

/**
 * Transaction filter options for the filter chips.
 */
enum class TransactionFilter(val displayName: String) {
    ALL("All"),
    DEBIT("Debit"),
    CREDIT("Credit"),
    AUTO("Auto")
}

/**
 * Represents a group of transactions for a specific date (e.g., "TODAY", "YESTERDAY", "Mar 15").
 */
data class TransactionGroup(
    val dateLabel: String,
    val totalAmount: Double,
    val isNetPositive: Boolean,
    val transactions: List<TransactionUiModel>
)

/**
 * Navigation events from Transaction screen.
 */
sealed class TransactionNavEvent {
    object NavigateToAddTransaction : TransactionNavEvent()
    object NavigateToDashboard : TransactionNavEvent()
    object NavigateToBudgets : TransactionNavEvent()
    object NavigateToGoals : TransactionNavEvent()
    data class NavigateToTransactionDetail(val transactionId: Long) : TransactionNavEvent()
}
