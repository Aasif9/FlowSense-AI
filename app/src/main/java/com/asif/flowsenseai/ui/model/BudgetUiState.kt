package com.asif.flowsenseai.ui.model

/**
 * UI state for Budget screen.
 */
data class BudgetUiState(
    val selectedMonth: String = "March 2026",
    val budgets: List<BudgetCategory> = emptyList(),
    val showAlert: Boolean = true,
    val alertMessage: String = "Shopping Over Limit",
    val alertDescription: String = "You've exceeded your shopping budget by ₹450",
    val totalSaved: Double = 4200.0,
    val financialHealth: String = "Excellent"
)

/**
 * Represents a single budget category with spending information.
 */
data class BudgetCategory(
    val id: String,
    val name: String,
    val iconResId: Int,
    val spent: Double,
    val budget: Double,
    val percentage: Float,
    val status: BudgetStatus
)

/**
 * Budget status based on spending percentage.
 */
enum class BudgetStatus {
    EXHAUSTED,     // > 90% (Red)
    WARNING,       // 70-90% (Yellow) 
    ON_TRACK,      // < 70% (Green)
    LOW            // Very low usage
}
