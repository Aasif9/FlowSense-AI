package com.asif.flowsenseai.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asif.flowsenseai.domain.model.Expense
import com.asif.flowsenseai.ui.viewmodel.ExpenseViewModel
import com.asif.flowsenseai.ui.screen.components.ExpenseCard
import com.asif.flowsenseai.util.DateUtils
import com.asif.flowsenseai.util.NotificationUtils
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
/**
 * Main screen of the FlowSense AI expense tracker app.
 * Shows a list of expenses with a FAB to add dummy expenses and pull-to-refresh.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: ExpenseViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
            .getInstance(LocalContext.current.applicationContext as android.app.Application)
    ),
    onNavigateToPermission: () -> Unit = {}
) {
    val context = LocalContext.current
    
    // Collect expenses from ViewModel
    val expenses by viewModel.expenses.collectAsState()
    val totalSpent by viewModel.totalSpent.collectAsState()
    
    // Check if notification listener is enabled
    val isNotificationListenerEnabled = remember {
        NotificationUtils.isNotificationListenerEnabled(context)
    }
    
    // State for pull-to-refresh
    var isRefreshing by remember { mutableStateOf(false) }
    // 1. Use the new state initialization
    val pullToRefreshState = rememberPullToRefreshState()

// 2. Use LaunchedEffect watching the 'isRefreshing' boolean
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(1000) // Simulate network delay
            // Add your viewModel.refresh() call here if needed
            isRefreshing = false
        }
    }
    
    Scaffold(
        // Top bar with app name
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "FlowSense AI",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        
        // Floating Action Button to add dummy expense
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.addDummyExpense()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Expense"
                )
            }
        }
    ) { paddingValues ->
        // Main content with pull-to-refresh
        Box(
            modifier = Modifier
                .fillMaxSize()
                // NEW: Use the pullToRefresh modifier instead of nestedScroll
                .pullToRefresh(
                    isRefreshing = isRefreshing,
                    state = pullToRefreshState,
                    onRefresh = { isRefreshing = true }
                )
        )  {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                // Show notification permission banner if not enabled
                if (!isNotificationListenerEnabled) {
                    NotificationPermissionBanner(onNavigateToPermission = onNavigateToPermission)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Total spent card with green color and bigger font
                TotalSpentCard(totalSpent = totalSpent, viewModel = viewModel)
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Expenses list
                if (expenses.isEmpty()) {
                    // Empty state
                    EmptyState()
                } else {
                    // List of expenses with better spacing
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(expenses) { expense ->
                            ExpenseCard(
                                expense = expense,
                                viewModel = viewModel,
                                onDelete = { viewModel.deleteExpense(expense) }
                            )
                        }
                    }
                }
            }
            
            // Pull-to-refresh indicator
            PullToRefreshDefaults.Indicator(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

/**
 * Card showing total amount spent with green color and bigger font.
 */
@Composable
fun TotalSpentCard(
    totalSpent: Double,
    viewModel: ExpenseViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50) // Green color
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Spent",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = viewModel.getFormattedAmount(totalSpent),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/**
 * Empty state when no expenses are present.
 */
@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No expenses yet!",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap the + button to add a dummy expense",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Banner to show when notification listener permission is not granted.
 * Encourages users to enable notification access for automatic expense tracking.
 */
@Composable
fun NotificationPermissionBanner(
    onNavigateToPermission: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Icon and text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notification Permission",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "Enable Notification Access",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "Automatically track UPI payments",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            
            // Right side: Button
            Button(
                onClick = onNavigateToPermission,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "Enable",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
