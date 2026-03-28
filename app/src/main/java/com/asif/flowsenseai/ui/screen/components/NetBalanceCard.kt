package com.asif.flowsenseai.ui.screen.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asif.flowsenseai.ui.theme.*

@Composable
fun NetBalanceCard(
    netBalance: Double,
    totalSpent: Double,
    totalIncome: Double,
    modifier: Modifier = Modifier
) {
    val isPositive = netBalance >= 0
    val balanceColor = if (isPositive) CreditGreenLight else DebitRedLight
    val balancePrefix = if (isPositive) "+" else "-"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Primary, PrimaryContainer)
                )
            )
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {

            // Label
            Text(
                text = "NET BALANCE",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp
                ),
                color = OnPrimaryContainer.copy(alpha = 0.75f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Big balance amount
            Text(
                text = "$balancePrefix₹${formatAmount(kotlin.math.abs(netBalance))}",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = balanceColor
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Divider
            HorizontalDivider(
                color = OnPrimary.copy(alpha = 0.15f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Total Spent + Total Income row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BalanceStat(
                    label = "TOTAL SPENT",
                    amount = "₹${formatAmount(totalSpent)}",
                    amountColor = OnPrimary
                )
                BalanceStat(
                    label = "TOTAL INCOME",
                    amount = "₹${formatAmount(totalIncome)}",
                    amountColor = OnPrimary
                )
            }
        }
    }
}

@Composable
private fun BalanceStat(
    label: String,
    amount: String,
    amountColor: Color
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.sp
            ),
            color = OnPrimaryContainer.copy(alpha = 0.65f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = amount,
            style = MaterialTheme.typography.titleMedium,
            color = amountColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NetBalanceCardPreview() {
    FlowSenseAITheme {
        NetBalanceCard(
            netBalance = 12460.0,
            totalSpent = 12540.0,
            totalIncome = 25000.0,
            modifier = Modifier.padding(16.dp)
        )
    }
}
