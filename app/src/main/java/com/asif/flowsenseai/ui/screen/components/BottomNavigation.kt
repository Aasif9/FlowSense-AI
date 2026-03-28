package com.asif.flowsenseai.ui.screen.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.asif.flowsenseai.ui.theme.*

private data class NavItem(
    val label: String,
    val icon: ImageVector
)

private val navItems = listOf(
    NavItem("Dashboard", Icons.Outlined.Dashboard),
    NavItem("Transactions", Icons.Outlined.Receipt),
    NavItem("Budgets", Icons.Outlined.AccountBalance),
    NavItem("Goals", Icons.Outlined.TrackChanges)
)

@Composable
fun FlowSenseBottomNav(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = SurfaceContainerLowest.copy(alpha = 0.92f),
        tonalElevation = 0.dp
    ) {
        navItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                    indicatorColor = SurfaceContainerHigh
                )
            )
        }
    }
}
