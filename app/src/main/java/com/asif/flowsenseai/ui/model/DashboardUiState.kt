package com.asif.flowsenseai.ui.model

/**
 * Complete UI state for the Dashboard screen.
 * ViewModel owns this — composables only read it.
 * Sealed state handles loading/error/success cleanly.
 */
data class DashboardUiState(
    val selectedMonth: String = "",
    val totalExpense: Double = 0.0,
    val totalIncome: Double = 0.0,
    val netBalance: Double = 0.0,
    val categoryBreakdown: List<CategoryUiModel> = emptyList(),
    val recentTransactions: List<TransactionUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class CategoryUiModel(
    val id: Long,
    val name: String,
    val iconResId: Int,           // drawable resource id
    val totalAmount: Double,
    val percentage: Float,        // 0f to 1f
    val colorHex: String = "#24389C"
)

data class TransactionUiModel(
    val id: Long,
    val merchantName: String,
    val category: String,
    val amount: Double,
    val isDebit: Boolean,
    val isAutoDetected: Boolean,  // shows AUTO badge
    val timeDisplay: String,      // "12:45 PM", "Yesterday", "Mar 01"
    val bankName: String,         // "HDFC Debit", "ICICI Bank"
    val iconResId: Int
)

/** Screen-level navigation state */
sealed class DashboardNavEvent {
    object NavigateToTransactions : DashboardNavEvent()
    object NavigateToBudgets : DashboardNavEvent()
    object NavigateToGoals : DashboardNavEvent()
    object NavigateToAddExpense : DashboardNavEvent()
    data class NavigateToCategoryDetail(val categoryId: Long) : DashboardNavEvent()
}
