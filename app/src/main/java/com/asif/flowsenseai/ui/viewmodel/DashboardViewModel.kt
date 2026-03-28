package com.asif.flowsenseai.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asif.flowsenseai.domain.repository.ExpenseRepository
import com.asif.flowsenseai.ui.model.CategoryUiModel
import com.asif.flowsenseai.ui.model.DashboardUiState
import com.asif.flowsenseai.ui.model.TransactionUiModel
import com.asif.flowsenseai.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import com.asif.flowsenseai.R
import com.asif.flowsenseai.domain.model.Expense

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    // One-shot navigation events (not part of persistent UI state)
    private val _navEvents = MutableSharedFlow<com.asif.flowsenseai.ui.model.DashboardNavEvent>()
    val navEvents = _navEvents.asSharedFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    private var currentMonth: YearMonth = YearMonth.now()

    init {
        loadDashboardData()
    }

    // ── Public events from UI ─────────────────────────────────────────────────

    @RequiresApi(Build.VERSION_CODES.O)
    fun onNextMonth() {
        currentMonth = currentMonth.plusMonths(1)
        loadDashboardData()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun onPreviousMonth() {
        currentMonth = currentMonth.minusMonths(1)
        loadDashboardData()
    }

    fun onViewAllTransactionsClicked() {
        viewModelScope.launch {
            _navEvents.emit(com.asif.flowsenseai.ui.model.DashboardNavEvent.NavigateToTransactions)
        }
    }

    fun onAddExpenseClicked() {
        viewModelScope.launch {
            _navEvents.emit(com.asif.flowsenseai.ui.model.DashboardNavEvent.NavigateToAddExpense)
        }
    }

    fun onCategoryClicked(categoryId: Long) {
        viewModelScope.launch {
            _navEvents.emit(com.asif.flowsenseai.ui.model.DashboardNavEvent.NavigateToCategoryDetail(categoryId))
        }
    }

    // ── Data loading ──────────────────────────────────────────────────────────

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val monthLabel = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))

                repository.getAllExpenses().collect { expenses ->
                    // Filter to current month
                    val monthlyExpenses = expenses.filter { expense ->
                        val expenseMonth = YearMonth.from(
                            java.time.Instant.ofEpochMilli(expense.date)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                        )
                        expenseMonth == currentMonth
                    }

                    val debits = monthlyExpenses.filter { it.type == "debit" }
                    val credits = monthlyExpenses.filter { it.type == "credit" }

                    val totalExpense = debits.sumOf { it.amount }
                    val totalIncome = credits.sumOf { it.amount }
                    val netBalance = totalIncome - totalExpense

                    val categories = buildCategoryBreakdown(debits, totalExpense)
                    val recent = buildRecentTransactions(monthlyExpenses.take(20))

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            selectedMonth = monthLabel,
                            totalExpense = totalExpense,
                            totalIncome = totalIncome,
                            netBalance = netBalance,
                            categoryBreakdown = categories,
                            recentTransactions = recent
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    /**
     * Groups expenses by category and calculates percentages.
     * Pure business logic — belongs here, NOT in composable.
     */
    private fun buildCategoryBreakdown(
        debits: List<Expense>,
        total: Double
    ): List<CategoryUiModel> {
        if (total == 0.0) return emptyList()

        return debits
            .groupBy { it.category }
            .map { (category, expenses) ->
                val categoryTotal = expenses.sumOf { it.amount }
                val percentage = (categoryTotal / total).toFloat()
                CategoryUiModel(
                    id = category.hashCode().toLong(),
                    name = category,
                    iconResId = getCategoryIcon(category),
                    totalAmount = categoryTotal,
                    percentage = percentage,
                    colorHex = getCategoryColor(category)
                )
            }
            .sortedByDescending { it.totalAmount }
            .take(5) // Show top 5 categories on dashboard
    }

    /**
     * Maps domain Expense list to UI-ready TransactionUiModel.
     * All formatting happens here — composable gets ready-to-display strings.
     */
    private fun buildRecentTransactions(expenses: List<Expense>): List<TransactionUiModel> {
        return expenses.map { expense ->
            TransactionUiModel(
                id = expense.id,
                merchantName = expense.merchant,
                category = expense.category,
                amount = expense.amount,
                isDebit = expense.type == "debit",
                isAutoDetected = true, // All notification-detected are AUTO
                timeDisplay = DateUtils.formatReadableDate(expense.date),
                bankName = "UPI",
                iconResId = getCategoryIcon(expense.category)
            )
        }
    }

    // ── Icon/color mapping (could be moved to a mapper class later) ───────────

    private fun getCategoryIcon(category: String): Int {
        return when (category.lowercase()) {
            "food", "food & dining", "food & drinks" -> R.drawable.ic_category_food
            "transport", "travel" -> R.drawable.ic_category_food
            "shopping" -> R.drawable.ic_category_food
            "bills", "utilities" -> R.drawable.ic_category_food
            "health", "medical" -> R.drawable.ic_category_food
            "entertainment" -> R.drawable.ic_category_food
            "income", "salary" -> R.drawable.ic_category_food
            else -> R.drawable.ic_category_food
        }
    }
//    private fun getCategoryIcon(category: String): Int {
//        return when (category.lowercase()) {
//            "food", "food & dining", "food & drinks" -> R.drawable.ic_category_food
//            "transport", "travel" -> R.drawable.ic_category_transport
//            "shopping" -> R.drawable.ic_category_shopping
//            "bills", "utilities" -> R.drawable.ic_category_bills
//            "health", "medical" -> R.drawable.ic_category_health
//            "entertainment" -> R.drawable.ic_category_entertainment
//            "income", "salary" -> R.drawable.ic_category_income
//            else -> R.drawable.ic_category_other
//        }
//    }

    private fun getCategoryColor(category: String): String {
        return when (category.lowercase()) {
            "food", "food & dining" -> "#FF6B35"
            "transport" -> "#24389C"
            "shopping" -> "#7B2FBE"
            "bills" -> "#006688"
            "health" -> "#006E1C"
            else -> "#24389C"
        }
    }
}
