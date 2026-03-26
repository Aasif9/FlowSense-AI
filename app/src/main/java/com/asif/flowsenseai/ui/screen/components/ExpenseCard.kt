package com.asif.flowsenseai.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asif.flowsenseai.domain.model.Expense
import com.asif.flowsenseai.ui.viewmodel.ExpenseViewModel
import com.asif.flowsenseai.util.DateUtils

/**
 * Beautiful reusable card component for displaying individual expenses.
 * Features category icons, readable date formatting, and modern Material3 design.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseCard(
    expense: Expense,
    viewModel: ExpenseViewModel,
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Category icon and expense details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Category icon with background
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(getCategoryColor(expense.category).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(expense.category),
                        contentDescription = expense.category,
                        tint = getCategoryColor(expense.category),
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Expense details
                Column {
                    Text(
                        text = viewModel.getFormattedAmount(expense.amount),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = expense.merchant,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = expense.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = getCategoryColor(expense.category),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = " • ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = DateUtils.formatReadableDate(expense.date),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Right side: Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Expense",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Get the appropriate icon for each expense category.
 */
@Composable
fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "food", "restaurant", "dining" -> Icons.Default.LocalDining
        "shopping", "retail" -> Icons.Default.ShoppingCart
        "transport", "travel", "car" -> Icons.Default.DirectionsCar
        "entertainment", "movie", "games" -> Icons.Default.Movie
        "home", "rent", "utilities" -> Icons.Default.Home
        else -> Icons.Default.MoreHoriz
    }
}

/**
 * Get the appropriate color for each expense category.
 */
@Composable
fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "food", "restaurant", "dining" -> Color(0xFFFF6B35) // Orange
        "shopping", "retail" -> Color(0xFF4ECDC4) // Teal
        "transport", "travel", "car" -> Color(0xFF45B7D1) // Blue
        "entertainment", "movie", "games" -> Color(0xFF96CEB4) // Green
        "home", "rent", "utilities" -> Color(0xFFDDA0DD) // Plum
        else -> MaterialTheme.colorScheme.primary
    }
}
