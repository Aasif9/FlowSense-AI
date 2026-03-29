package com.asif.flowsenseai.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asif.flowsenseai.R
import com.asif.flowsenseai.ui.model.*
import com.asif.flowsenseai.ui.screen.components.FlowSenseBottomNav
import com.asif.flowsenseai.ui.theme.*

// ─── Screen entry point ───────────────────────────────────────────────────────

@Composable
fun BudgetScreen(
    uiState: BudgetUiState = BudgetUiState(),
    selectedNavIndex: Int = 2,
    onNavItemSelected: (Int) -> Unit = {},
    onAddBudgetClicked: () -> Unit = {},
    onPreviousMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {},
    onBudgetClicked: (BudgetCategory) -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            BudgetTopBar(
                currentMonth = uiState.selectedMonth,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth
            )
        },
        bottomBar = {
            FlowSenseBottomNav(
                selectedIndex = selectedNavIndex,
                onItemSelected = onNavItemSelected
            )
        },
        floatingActionButton = {
            BudgetFloatingActionButton(onClick = onAddBudgetClicked)
        }
    ) { padding ->
        BudgetContent(
            uiState = uiState,
            onBudgetClicked = onBudgetClicked,
            modifier = Modifier.padding(padding)
        )
    }
}

// ─── Top Bar ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BudgetTopBar(
    currentMonth: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = currentMonth,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = ManropeFontFamily
                ),
                color = MaterialTheme.colorScheme.primary
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onPreviousMonth,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_category_food),
                    contentDescription = "Previous month",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            IconButton(
                onClick = onNextMonth,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_category_food),
                    contentDescription = "Next month",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
        )
    )
}

// ─── Main Content ─────────────────────────────────────────────────────────────

@Composable
fun BudgetContent(
    uiState: BudgetUiState,
    onBudgetClicked: (BudgetCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            start = 24.dp,
            end = 24.dp,
            top = 24.dp,
            bottom = 140.dp
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Budget Alert Banner
        if (uiState.showAlert) {
            item {
                BudgetAlertBanner(
                    alertMessage = uiState.alertMessage,
                    alertDescription = uiState.alertDescription,
                    onDismiss = { /* Handle dismiss */ }
                )
            }
        }

        // Hero Section
        item {
           // BudgetHeroSection(onAddBudgetClicked = onAddBudgetClicked)
        }

        // Budget Categories
        items(uiState.budgets.size) { index ->
            val budget = uiState.budgets[index]
            BudgetCategoryCard(
                budget = budget,
                onClick = { onBudgetClicked(budget) }
            )
        }

        // Smart Forecast Card
        item {
            SmartForecastCard(
                totalSaved = uiState.totalSaved,
                financialHealth = uiState.financialHealth
            )
        }
    }
}

// ─── Budget Alert Banner ─────────────────────────────────────────────────────

@Composable
fun BudgetAlertBanner(
    alertMessage: String,
    alertDescription: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = RoundedCornerShape(50.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Warning Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Alert",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            // Alert Text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Alert: $alertMessage",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily
                    ),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = alertDescription,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f)
                )
            }

            // Dismiss Button
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

// ─── Hero Section ─────────────────────────────────────────────────────────────

@Composable
fun BudgetHeroSection(onAddBudgetClicked: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(
                text = "Monthly Budgets",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = ManropeFontFamily
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Manage your architectural financial plan.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Button(
            onClick = onAddBudgetClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = CircleShape,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add budget",
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Add Budget",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

// ─── Budget Category Card ────────────────────────────────────────────────────

@Composable
fun BudgetCategoryCard(
    budget: BudgetCategory,
    onClick: () -> Unit
) {
    var progressStarted by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (progressStarted) budget.percentage / 100f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "budget_progress"
    )
    
    LaunchedEffect(Unit) { progressStarted = true }

    val (containerColor, progressColor, textColor) = when (budget.status) {
        BudgetStatus.EXHAUSTED -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.error
        )
        BudgetStatus.WARNING -> Triple(
            MaterialTheme.colorScheme.surfaceContainerLowest,
            Color(0xFFEAB308), // Amber
            MaterialTheme.colorScheme.onSurface
        )
        else -> Triple(
            MaterialTheme.colorScheme.surfaceContainerLowest,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.secondary
        )
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(50.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            //shadowElevation = 12.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Icon and Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                when (budget.status) {
                                    BudgetStatus.EXHAUSTED -> MaterialTheme.colorScheme.errorContainer
                                    BudgetStatus.WARNING -> MaterialTheme.colorScheme.surfaceContainerHighest
                                    else -> MaterialTheme.colorScheme.secondaryContainer
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(budget.iconResId),
                            contentDescription = budget.name,
                            modifier = Modifier.size(24.dp),
                            tint = when (budget.status) {
                                BudgetStatus.EXHAUSTED -> MaterialTheme.colorScheme.error
                                BudgetStatus.WARNING -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSecondaryContainer
                            }
                        )
                    }

                    Column {
                        Text(
                            text = budget.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = ManropeFontFamily
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${budget.percentage.toInt()}% Exhausted",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium,
                                fontFamily = InterFontFamily
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Amount Column
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "₹${formatAmount(budget.spent)}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = ManropeFontFamily
                        ),
                        color = textColor
                    )
                    Text(
                        text = "of ₹${formatAmount(budget.budget)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerHighest,
                        RoundedCornerShape(50.dp)
                    )
                    .clip(RoundedCornerShape(50.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .background(
                            progressColor,
                            RoundedCornerShape(50.dp)
                        )
                )
            }
        }
    }
}

// ─── Smart Forecast Card ─────────────────────────────────────────────────────

@Composable
fun SmartForecastCard(
    totalSaved: Double,
    financialHealth: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(50.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Background decoration
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(120.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(32.dp)
            ) {
                Text(
                    text = "Smart Forecast",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = ManropeFontFamily
                    ),
                    color = Color.White
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Based on your current spending, you are likely to save ₹${formatAmount(totalSaved)} more this month than in February.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 20.sp
                )

                Spacer(Modifier.height(16.dp))

                // Financial Health Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_category_food),
                        contentDescription = "Trending up",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Text(
                        text = "Financial Health: $financialHealth",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = InterFontFamily
                        ),
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ─── Floating Action Button ───────────────────────────────────────────────────

@Composable
fun BudgetFloatingActionButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 12.dp,
            //shadowElevation = 32.dp
        )
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_category_food),
            contentDescription = "Insights",
            modifier = Modifier.size(24.dp)
        )
    }
}

// ─── Helper Functions ─────────────────────────────────────────────────────────

private fun formatAmount(amount: Double): String {
    return when {
        amount >= 100_000 -> "%.1fL".format(amount / 100_000)
        amount >= 1_000 -> "%,.0f".format(amount)
        else -> "%.0f".format(amount)
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun BudgetScreenPreview() {
    val sampleBudgets = listOf(
        BudgetCategory(
            id = "1",
            name = "Shopping",
            iconResId = R.drawable.ic_category_food,
            spent = 4800.0,
            budget = 5000.0,
            percentage = 96f,
            status = BudgetStatus.EXHAUSTED
        ),
        BudgetCategory(
            id = "2",
            name = "Food & Drinks",
            iconResId = R.drawable.ic_category_food,
            spent = 2400.0,
            budget = 3000.0,
            percentage = 80f,
            status = BudgetStatus.WARNING
        ),
        BudgetCategory(
            id = "3",
            name = "Transport",
            iconResId = R.drawable.ic_category_food,
            spent = 900.0,
            budget = 2000.0,
            percentage = 45f,
            status = BudgetStatus.ON_TRACK
        ),
        BudgetCategory(
            id = "4",
            name = "Entertainment",
            iconResId = R.drawable.ic_category_food,
            spent = 500.0,
            budget = 2500.0,
            percentage = 20f,
            status = BudgetStatus.LOW
        )
    )

    FlowSenseAITheme {
        BudgetScreen(
            uiState = BudgetUiState(
                budgets = sampleBudgets
            ),
            onNavItemSelected = {},
            onAddBudgetClicked = {},
            onPreviousMonth = {},
            onNextMonth = {},
            onBudgetClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BudgetAlertBannerPreview() {
    FlowSenseAITheme {
        BudgetAlertBanner(
            alertMessage = "Shopping Over Limit",
            alertDescription = "You've exceeded your shopping budget by ₹450",
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SmartForecastCardPreview() {
    FlowSenseAITheme {
        SmartForecastCard(
            totalSaved = 4200.0,
            financialHealth = "Excellent"
        )
    }
}
