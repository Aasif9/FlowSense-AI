package com.asif.flowsenseai.ui.screen.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.asif.flowsenseai.R
import com.asif.flowsenseai.ui.model.CategoryUiModel
import com.asif.flowsenseai.ui.theme.*

@Composable
fun CategoryInsightItem(
    category: CategoryUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animate the progress bar on first appearance
    var progressStarted by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (progressStarted) category.percentage else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 150),
        label = "progress_${category.id}"
    )

    LaunchedEffect(category.id) {
        progressStarted = true
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Category icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerLowest),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = category.iconResId),
                        contentDescription = category.name,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Percentage badge
                PercentageBadge(
                    percentage = (category.percentage * 100).toInt()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "₹${formatAmount(category.totalAmount)}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(50.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = SurfaceContainer,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
    }
}

@Composable
private fun PercentageBadge(percentage: Int) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(SurfaceContainerHigh)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryInsightItemPreview() {
    FlowSenseAITheme {
        CategoryInsightItem(
            category = CategoryUiModel(
                id = 1L,
                name = "Food & Dining",
                iconResId = R.drawable.ic_launcher_foreground,
               // iconResId = R.drawable.ic_category_food,
                totalAmount = 3200.0,
                percentage = 0.40f
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
