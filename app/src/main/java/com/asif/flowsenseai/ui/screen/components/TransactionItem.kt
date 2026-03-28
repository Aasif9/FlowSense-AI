package com.asif.flowsenseai.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.asif.flowsenseai.R
import com.asif.flowsenseai.ui.model.TransactionUiModel
import com.asif.flowsenseai.ui.theme.*

@Composable
fun TransactionItem(
    transaction: TransactionUiModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = transaction.iconResId),
                contentDescription = transaction.merchantName,
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Merchant + meta
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = transaction.merchantName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (transaction.isAutoDetected) {
                    Spacer(modifier = Modifier.width(6.dp))
                    AutoBadge()
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${transaction.category} • ${transaction.timeDisplay}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Amount + bank
        Column(horizontalAlignment = Alignment.End) {
            val amountPrefix = if (transaction.isDebit) "-" else "+"
            val amountColor = if (transaction.isDebit) DebitRed else CreditGreen

            Text(
                text = "$amountPrefix₹${formatAmount(transaction.amount)}",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = amountColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = transaction.bankName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
            )
        }
    }
}

@Composable
private fun AutoBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(TertiaryFixed)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "AUTO",
            style = MaterialTheme.typography.labelSmall,
            color = OnTertiaryFixed
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionItemPreview() {
    FlowSenseAITheme {
        TransactionItem(
            transaction = TransactionUiModel(
                id = 1L,
                merchantName = "Swiggy",
                category = "Food & Drinks",
                amount = 450.0,
                isDebit = true,
                isAutoDetected = true,
                timeDisplay = "12:45 PM",
                bankName = "HDFC Debit",
                iconResId = R.drawable.ic_launcher_foreground,
                // iconResId = R.drawable.ic_category_food,
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
