package com.asif.flowsenseai.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asif.flowsenseai.ui.model.DashboardNavEvent
import com.asif.flowsenseai.ui.model.DashboardUiState
import com.asif.flowsenseai.ui.screen.components.*
import com.asif.flowsenseai.ui.theme.SurfaceContainerHigh
import com.asif.flowsenseai.ui.viewmodel.DashboardViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * DashboardScreen — Entry point for the dashboard feature.
 *
 * Responsibilities:
 * ✅ Collect UI state from ViewModel
 * ✅ Handle one-shot navigation events
 * ✅ Pass callbacks DOWN to content composables
 * ✅ Own the Scaffold + BottomNav + FAB
 *
 * Does NOT contain any layout or business logic.
 */
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToTransactions: () -> Unit,
    onNavigateToBudgets: () -> Unit,
    onNavigateToGoals: () -> Unit,
    onNavigateToAddExpense: () -> Unit,
    onNavigateToCategoryDetail: (Long) -> Unit,
    selectedNavIndex: Int = 0,
    onNavItemSelected: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Collect one-shot nav events
    LaunchedEffect(Unit) {
        viewModel.navEvents.collectLatest { event ->
            when (event) {
                is DashboardNavEvent.NavigateToTransactions -> onNavigateToTransactions()
                is DashboardNavEvent.NavigateToAddExpense -> onNavigateToAddExpense()
                is DashboardNavEvent.NavigateToCategoryDetail -> onNavigateToCategoryDetail(event.categoryId)
                is DashboardNavEvent.NavigateToBudgets -> onNavigateToBudgets()
                is DashboardNavEvent.NavigateToGoals -> onNavigateToGoals()
            }
        }
    }

    DashboardScaffold(
        uiState = uiState,
        selectedNavIndex = selectedNavIndex,
        onNavItemSelected = onNavItemSelected,
        onPreviousMonth = viewModel::onPreviousMonth,
        onNextMonth = viewModel::onNextMonth,
        onViewAllTransactions = viewModel::onViewAllTransactionsClicked,
        onAddExpense = viewModel::onAddExpenseClicked,
        onCategoryClicked = viewModel::onCategoryClicked
    )
}

/**
 * DashboardScaffold — Pure layout shell.
 * Stateless — receives all data and callbacks as parameters.
 * Easily previewable.
 */
@Composable
private fun DashboardScaffold(
    uiState: DashboardUiState,
    selectedNavIndex: Int,
    onNavItemSelected: (Int) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onViewAllTransactions: () -> Unit,
    onAddExpense: () -> Unit,
    onCategoryClicked: (Long) -> Unit
) {
    val listState = rememberLazyListState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            DashboardFab(onClick = onAddExpense)
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            FlowSenseBottomNav(
                selectedIndex = selectedNavIndex,
                onItemSelected = onNavItemSelected
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                uiState.isLoading -> DashboardLoadingState()
                uiState.error != null -> DashboardErrorState(message = uiState.error)
                else -> DashboardContent(
                    uiState = uiState,
                    listState = listState,
                    onPreviousMonth = onPreviousMonth,
                    onNextMonth = onNextMonth,
                    onViewAllTransactions = onViewAllTransactions,
                    onCategoryClicked = onCategoryClicked
                )
            }
        }
    }
}

/**
 * DashboardContent — The actual scrollable content.
 * Stateless, pure rendering composable.
 */
@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    listState: LazyListState = rememberLazyListState(),
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onViewAllTransactions: () -> Unit,
    onCategoryClicked: (Long) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp) // space above bottom nav
    ) {
        // Month header
        item {
            MonthHeader(
                month = uiState.selectedMonth,
                onPrevious = onPreviousMonth,
                onNext = onNextMonth,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )
        }

        // Net Balance Card
        item {
            NetBalanceCard(
                netBalance = uiState.netBalance,
                totalSpent = uiState.totalExpense,
                totalIncome = uiState.totalIncome,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        // Category Insights
        item {
            Spacer(modifier = Modifier.height(28.dp))
            SectionHeader(
                title = "Category Insights",
                actionLabel = "View Details",
                onActionClick = onViewAllTransactions,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (uiState.categoryBreakdown.isEmpty()) {
            item {
                EmptySectionPlaceholder(
                    message = "No expenses this month",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        } else {
            items(
                items = uiState.categoryBreakdown,
                key = { it.id }
            ) { category ->
                CategoryInsightItem(
                    category = category,
                    onClick = { onCategoryClicked(category.id) },
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 10.dp)
                        .animateItem()
                )
            }
        }

        // Recent Activity
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "Recent Activity",
                actionLabel = null,
                onActionClick = null,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (uiState.recentTransactions.isEmpty()) {
            item {
                EmptySectionPlaceholder(
                    message = "No transactions yet",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        } else {
            items(
                items = uiState.recentTransactions,
                key = { it.id }
            ) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 4.dp)
                        .animateItem()
                )
            }
        }
    }
}

@Composable
private fun DashboardLoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun DashboardErrorState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Something went wrong:\n$message",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun DashboardFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 12.dp,
            pressedElevation = 6.dp
        ),
        modifier = Modifier.size(56.dp)
    ) {
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Default.Add,
            contentDescription = "Add expense",
            modifier = Modifier.size(24.dp)
        )
    }
}
