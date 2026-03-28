package com.asif.flowsenseai.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.asif.flowsenseai.ui.screen.DashboardScreen
// Import future screens here as you build them:
// import com.asif.flowsenseai.ui.screen.TransactionsScreen
// import com.asif.flowsenseai.ui.screen.BudgetsScreen
// import com.asif.flowsenseai.ui.screen.GoalsScreen

/**
 * Route constants — single source of truth for navigation.
 * Add new routes here as features expand.
 */
object Routes {
    const val DASHBOARD = "dashboard"
    const val TRANSACTIONS = "transactions"
    const val BUDGETS = "budgets"
    const val GOALS = "goals"
    const val ADD_EXPENSE = "add_expense"
    const val CATEGORY_DETAIL = "category_detail/{categoryId}"

    fun categoryDetail(categoryId: Long) = "category_detail/$categoryId"
}

/** Maps route to bottom nav index — used to keep bottom nav in sync */
fun routeToNavIndex(route: String?): Int = when (route) {
    Routes.DASHBOARD -> 0
    Routes.TRANSACTIONS -> 1
    Routes.BUDGETS -> 2
    Routes.GOALS -> 3
    else -> 0
}

fun navIndexToRoute(index: Int): String = when (index) {
    0 -> Routes.DASHBOARD
    1 -> Routes.TRANSACTIONS
    2 -> Routes.BUDGETS
    3 -> Routes.GOALS
    else -> Routes.DASHBOARD
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val selectedNavIndex = routeToNavIndex(currentRoute)

    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD
    ) {
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
                    navController.navigate(Routes.GOALS)
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
            PlaceholderScreen("Transactions") // Replace with TransactionsScreen()
        }
        composable(Routes.BUDGETS) {
            PlaceholderScreen("Budgets") // Replace with BudgetsScreen()
        }
        composable(Routes.GOALS) {
            PlaceholderScreen("Goals") // Replace with GoalsScreen()
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
