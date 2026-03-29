package com.asif.flowsenseai.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asif.flowsenseai.ui.model.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asif.flowsenseai.ui.screen.components.*
import com.asif.flowsenseai.ui.theme.*
import com.asif.flowsenseai.ui.viewmodel.TransactionViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * TransactionScreen — Shows all transactions with filtering and grouping.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel = hiltViewModel(),
    selectedNavIndex: Int = 1,
    onNavItemSelected: (Int) -> Unit,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToBudgets: () -> Unit,
    onNavigateToGoals: () -> Unit,
    onNavigateToIncome: () -> Unit = {},
    onTransactionClicked: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navEvents.collectLatest { event ->
            when (event) {
                is TransactionNavEvent.NavigateToAddTransaction -> onNavigateToAddTransaction()
                is TransactionNavEvent.NavigateToDashboard -> onNavigateToDashboard()
                is TransactionNavEvent.NavigateToBudgets -> onNavigateToBudgets()
                is TransactionNavEvent.NavigateToGoals -> onNavigateToGoals()
                is TransactionNavEvent.NavigateToTransactionDetail -> {
                    onTransactionClicked(event.transactionId)
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Transactions",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToDashboard) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to Dashboard",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { 
                            println("DEBUG: Income button clicked - navigating to income screen")
                            onNavigateToIncome()
                        }
                    ) {
                        Text(
                            text = "Income",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = viewModel::onAddTransactionClicked,
                modifier = Modifier.size(56.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add transaction",
                    modifier = Modifier.size(24.dp)
                )
            }
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
            // Debug logging for transaction navigation
            LaunchedEffect(Unit) {
                println("DEBUG: TransactionScreen - Screen initialized")
            }
            when (val state = uiState) {
                is TransactionScreenState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is TransactionScreenState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Something went wrong:\n${state.message}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is TransactionScreenState.Success -> {
                    TransactionContent(
                        state = state,
                        onFilterSelected = viewModel::onFilterSelected,
                        onPreviousMonth = viewModel::onPreviousMonth,
                        onNextMonth = viewModel::onNextMonth,
                        onTransactionClicked = viewModel::onTransactionClicked
                    )
                }
            }
        }
    }
}

/**
 * TransactionContent — Main content with filter chips and transaction groups.
 */
@Composable
fun TransactionContent(
    state: TransactionScreenState.Success,
    onFilterSelected: (TransactionFilter) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onTransactionClicked: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp) // space above bottom nav
    ) {
        // Month header
        item {
            MonthHeader(
                month = state.selectedMonth,
                onPrevious = onPreviousMonth,
                onNext = onNextMonth,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )
        }

        // Filter chips
        item {
            TransactionFilterChips(
                selectedFilter = state.selectedFilter,
                onFilterSelected = onFilterSelected,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Transaction groups
        if (state.groupedTransactions.isEmpty()) {
            item {
                EmptySectionPlaceholder(
                    message = "No transactions found",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        } else {
            items(
                items = state.groupedTransactions,
                key = { it.dateLabel }
            ) { group ->
                TransactionGroupSection(
                    group = group,
                    onTransactionClicked = onTransactionClicked,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                )
            }
        }
    }
}

/**
 * TransactionFilterChips — Filter selection chips.
 */
@Composable
fun TransactionFilterChips(
    selectedFilter: TransactionFilter,
    onFilterSelected: (TransactionFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TransactionFilter.values().forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = filter.displayName,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.LightGray,          // Use this instead of backgroundColor
                    labelColor = Color.Black,                  // Use this instead of contentColor
                    selectedContainerColor = Color.Blue,       // Use this instead of selectedBackgroundColor
                    selectedLabelColor = Color.White,          // Use this instead of selectedContentColor
                    iconColor = Color.DarkGray,                // For unselected icons
                    selectedLeadingIconColor = Color.White     // For the checkmark/leading icon when selected
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * TransactionGroupSection — Group of transactions for a specific date.
 */
@Composable
fun TransactionGroupSection(
    group: TransactionGroup,
    onTransactionClicked: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Date header with total
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = group.dateLabel,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = if (group.isNetPositive) "+₹${formatAmount(kotlin.math.abs(group.totalAmount))}" 
                      else "-₹${formatAmount(kotlin.math.abs(group.totalAmount))}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (group.isNetPositive) CreditGreen else DebitRed
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Transactions
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            group.transactions.forEach { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onClick = { 
                        println("DEBUG: TransactionItem clicked - Transaction ID: ${transaction.id}")
                        onTransactionClicked(transaction.id) 
                    },                    //modifier = Modifier.animateItem()
                )
            }
        }
    }
}

/**
 * Central amount formatter. Called from all components.
 * Keep in one place so format changes apply everywhere.
 */
private fun formatAmount(amount: Double): String {
    return when {
        amount >= 100_000 -> "%.1fL".format(amount / 100_000)
        amount >= 1_000 -> "%,.0f".format(amount)
        else -> "%.0f".format(amount)
    }
}
