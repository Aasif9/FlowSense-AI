package com.asif.flowsenseai.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.asif.flowsenseai.ui.screen.DashboardScreen
import com.asif.flowsenseai.ui.screen.TransactionScreen
import com.asif.flowsenseai.ui.screen.AddTransactionScreen
import com.asif.flowsenseai.ui.screen.NotificationPermissionScreen
import com.asif.flowsenseai.ui.screen.TransactionDetailScreen
import com.asif.flowsenseai.ui.screen.AddIncomeScreen
import com.asif.flowsenseai.ui.screen.SettingsScreen
import com.asif.flowsenseai.ui.screen.EditProfileScreen
import com.asif.flowsenseai.ui.screen.BudgetScreen
// Import future screens here as you build them:
// import com.asif.flowsenseai.ui.screen.BudgetsScreen
// import com.asif.flowsenseai.ui.screen.GoalsScreen

/**
 * Route constants — single source of truth for navigation.
 * Add new routes here as features expand.
 */
object Routes {
    const val PERMISSION = "permission"
    const val DASHBOARD = "dashboard"
    const val TRANSACTIONS = "transactions"
    const val BUDGETS = "budgets"
    const val SETTINGS = "settings"
    const val ADD_EXPENSE = "add_expense"
    const val TRANSACTION_DETAIL = "transaction_detail/{transactionId}"
    const val ADD_INCOME = "add_income"
    const val EDIT_PROFILE = "edit_profile"
    const val CATEGORY_DETAIL = "category_detail/{categoryId}"

    fun categoryDetail(categoryId: Long) = "category_detail/$categoryId"
    fun transactionDetail(id: Long) = "transaction_detail/$id"
}

/** Maps route to bottom nav index — used to keep bottom nav in sync */
fun routeToNavIndex(route: String?): Int = when (route) {
    Routes.DASHBOARD -> 0
    Routes.TRANSACTIONS -> 1
    Routes.BUDGETS -> 2
    Routes.SETTINGS -> 3
    else -> 0
}

fun navIndexToRoute(index: Int): String = when (index) {
    0 -> Routes.DASHBOARD
    1 -> Routes.TRANSACTIONS
    2 -> Routes.BUDGETS
    3 -> Routes.SETTINGS
    else -> Routes.DASHBOARD
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    startWithPermissionScreen: Boolean = false
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val selectedNavIndex = routeToNavIndex(currentRoute)

    val startDestination = if (startWithPermissionScreen) Routes.PERMISSION else Routes.DASHBOARD

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.PERMISSION) {
            NotificationPermissionScreen(
                onPermissionGranted = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.PERMISSION) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                selectedNavIndex = selectedNavIndex,
                onNavItemSelected = { index ->
                    val route = navIndexToRoute(index)
                    navController.navigate(route) {
                        popUpTo(Routes.DASHBOARD) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToTransactions = {
                    navController.navigate(Routes.TRANSACTIONS)
                },
                onNavigateToBudgets = {
                    navController.navigate(Routes.BUDGETS)
                },
                onNavigateToGoals = {
                    navController.navigate(Routes.SETTINGS)
                },
                onNavigateToAddExpense = {
                    navController.navigate(Routes.ADD_EXPENSE)
                },
                onNavigateToCategoryDetail = { categoryId ->
                    navController.navigate(Routes.categoryDetail(categoryId))
                }
            )
        }

        // Placeholder composables — replace with real screens as you build them
        composable(Routes.TRANSACTIONS) {
            TransactionScreen(
                selectedNavIndex = selectedNavIndex,
                onNavItemSelected = { index ->
                    val route = navIndexToRoute(index)
                    navController.navigate(route) {
                        popUpTo(Routes.DASHBOARD) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToAddTransaction = {
                    navController.navigate(Routes.ADD_EXPENSE)
                },
                onNavigateToDashboard = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                },
                onNavigateToBudgets = {
                    navController.navigate(Routes.BUDGETS)
                },
                onNavigateToGoals = {
                    navController.navigate(Routes.SETTINGS)
                },
                onNavigateToIncome = {
                    println("DEBUG: AppNavGraph - Navigating to income screen")
                    navController.navigate(Routes.ADD_INCOME)
                },
                onTransactionClicked = { transactionId ->
                    println("DEBUG: AppNavGraph - Navigating to transaction detail with ID: $transactionId")
                    navController.navigate(Routes.transactionDetail(transactionId))
                }
            )
        }
        composable(Routes.ADD_EXPENSE) {
            AddTransactionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Routes.TRANSACTION_DETAIL,
            arguments = listOf(
                androidx.navigation.navArgument("transactionId") {
                    type = androidx.navigation.NavType.LongType
                }
            )
        ) {
            TransactionDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    // Navigate to AddTransactionScreen with pre-filled data
                    navController.navigate(Routes.ADD_EXPENSE)
                }
            )
        }
        composable(Routes.ADD_INCOME) {
            AddIncomeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Routes.BUDGETS) {
            BudgetScreen(
                uiState = com.asif.flowsenseai.ui.model.BudgetUiState(), // replace with hiltViewModel() later
                selectedNavIndex = 2,
                onNavItemSelected = { index ->
                    navController.navigate(navIndexToRoute(index)) {
                        popUpTo(Routes.DASHBOARD) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onAddBudgetClicked = { /* Handle add budget */ },
                onPreviousMonth = { /* Handle previous month */ },
                onNextMonth = { /* Handle next month */ },
                onBudgetClicked = { budget -> /* Handle budget click */ }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                uiState = com.asif.flowsenseai.ui.model.SettingsUiState(), // replace with hiltViewModel() later
                selectedNavIndex = 3,
                onNavItemSelected = { index ->
                    navController.navigate(navIndexToRoute(index)) {
                        popUpTo(Routes.DASHBOARD) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onEditProfileClicked = {
                    navController.navigate(Routes.EDIT_PROFILE)
                },
                onFaceIdToggled = { /* static for now */ },
                onDarkModeToggled = { /* static for now */ },
                onLogOutClicked = { /* show dialog */ }
            )
        }
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(
                uiState = com.asif.flowsenseai.ui.model.EditProfileUiState(), // replace with hiltViewModel() later
                onNavigateBack = { navController.popBackStack() },
                onSaveClicked = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun PlaceholderScreen(name: String) {
    androidx.compose.foundation.layout.Box(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = "$name — Coming Soon",
            style = androidx.compose.material3.MaterialTheme.typography.titleLarge
        )
    }
}
