package com.asif.flowsenseai.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asif.flowsenseai.domain.repository.ExpenseRepository
import com.asif.flowsenseai.ui.model.*
import com.asif.flowsenseai.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import com.asif.flowsenseai.R
import com.asif.flowsenseai.domain.model.Expense

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransactionScreenState>(TransactionScreenState.Loading)
    val uiState: StateFlow<TransactionScreenState> = _uiState.asStateFlow()

    private val _navEvents = MutableSharedFlow<TransactionNavEvent>()
    val navEvents = _navEvents.asSharedFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    private var currentMonth: YearMonth = YearMonth.now()
    private var currentFilter: TransactionFilter = TransactionFilter.ALL
    private var allExpenses: List<Expense> = emptyList()

    init {
        loadTransactions()
    }

    // ── Public events from UI ─────────────────────────────────────────────────
    @RequiresApi(Build.VERSION_CODES.O)
    fun onNextMonth() {
        currentMonth = currentMonth.plusMonths(1)
        applyFilterAndGroup()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun onPreviousMonth() {
        currentMonth = currentMonth.minusMonths(1)
        applyFilterAndGroup()
    }

    fun onFilterSelected(filter: TransactionFilter) {
        currentFilter = filter
        applyFilterAndGroup()
    }

    fun onAddTransactionClicked() {
        viewModelScope.launch {
            _navEvents.emit(TransactionNavEvent.NavigateToAddTransaction)
        }
    }

    fun onTransactionClicked(transactionId: Long) {
        viewModelScope.launch {
            _navEvents.emit(TransactionNavEvent.NavigateToTransactionDetail(transactionId))
        }
    }

    // ── Data loading ──────────────────────────────────────────────────────────

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadTransactions() {
        viewModelScope.launch {
            try {
                repository.getAllExpenses().collect { expenses ->
                    allExpenses = expenses
                    applyFilterAndGroup()
                }
            } catch (e: Exception) {
                _uiState.value = TransactionScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Core logic: filters expenses by month and filter type, then groups by date.
     * This is the heart of the Transaction screen.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun applyFilterAndGroup() {
        _uiState.value = TransactionScreenState.Loading

        viewModelScope.launch {
            try {
                val monthLabel = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))

                // Filter to current month
                val monthlyExpenses = allExpenses.filter { expense ->
                    val expenseMonth = YearMonth.from(
                        Instant.ofEpochMilli(expense.date)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    )
                    expenseMonth == currentMonth
                }

                // Apply transaction filter
                val filteredExpenses = when (currentFilter) {
                    TransactionFilter.ALL -> monthlyExpenses
                    TransactionFilter.DEBIT -> monthlyExpenses.filter { it.type == "debit" }
                    TransactionFilter.CREDIT -> monthlyExpenses.filter { it.type == "credit" }
                    TransactionFilter.AUTO -> monthlyExpenses.filter { true } // All are auto-detected from notifications
                }

                // Group by date
                val grouped = groupTransactionsByDate(filteredExpenses)

                _uiState.value = TransactionScreenState.Success(
                    selectedMonth = monthLabel,
                    selectedFilter = currentFilter,
                    groupedTransactions = grouped
                )
            } catch (e: Exception) {
                _uiState.value = TransactionScreenState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Groups transactions by relative date (TODAY, YESTERDAY, Mar 15, etc.)
     * Calculates daily totals and net positivity.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun groupTransactionsByDate(expenses: List<Expense>): List<TransactionGroup> {
        if (expenses.isEmpty()) return emptyList()

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        return expenses
            .groupBy { expense ->
                val expenseDate = Instant.ofEpochMilli(expense.date)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                when {
                    expenseDate.isEqual(today) -> "TODAY"
                    expenseDate.isEqual(yesterday) -> "YESTERDAY"
                    expenseDate.year == today.year -> {
                        expenseDate.format(DateTimeFormatter.ofPattern("MMM dd"))
                    }
                    else -> {
                        expenseDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                    }
                }
            }
            .map { (dateLabel, dayExpenses) ->
                val totalAmount = dayExpenses.sumOf { expense ->
                    if (expense.type == "debit") -expense.amount else expense.amount
                }
                val isNetPositive = totalAmount >= 0

                val transactions = dayExpenses.map { expense ->
                    TransactionUiModel(
                        id = expense.id,
                        merchantName = expense.merchant,
                        category = expense.category,
                        amount = expense.amount,
                        isDebit = expense.type == "debit",
                        isAutoDetected = true, // All from notifications are AUTO
                        timeDisplay = DateUtils.formatReadableDate(expense.date),
                        bankName = "UPI",
                        iconResId = getCategoryIcon(expense.category)
                    )
                }.sortedByDescending { it.timeDisplay }

                TransactionGroup(
                    dateLabel = dateLabel,
                    totalAmount = totalAmount,
                    isNetPositive = isNetPositive,
                    transactions = transactions
                )
            }
            .sortedByDescending { group ->
                when (group.dateLabel) {
                    "TODAY" -> LocalDate.now().toEpochDay()
                    "YESTERDAY" -> LocalDate.now().minusDays(1).toEpochDay()
                    else -> {
                        // Try to parse the date for proper sorting
                        try {
                            val formatter = if (group.dateLabel.contains(",")) {
                                DateTimeFormatter.ofPattern("MMM dd, yyyy")
                            } else {
                                java.time.format.DateTimeFormatterBuilder()
                                    .appendPattern("MMM dd")
                                    .parseDefaulting(java.time.temporal.ChronoField.YEAR, LocalDate.now().year.toLong())
                                    .toFormatter()                            }
                            LocalDate.parse(group.dateLabel, formatter).toEpochDay()
                        } catch (e: Exception) {
                            0L // Fallback
                        }
                    }
                }
            }
    }

    // ── Icon mapping (could be moved to a mapper class later) ─────────────────

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
}
