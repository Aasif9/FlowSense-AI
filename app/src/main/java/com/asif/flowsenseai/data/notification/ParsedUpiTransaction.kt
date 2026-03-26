package com.asif.flowsenseai.data.notification

import com.asif.flowsenseai.domain.model.Expense

/**
 * Simple data class representing a parsed UPI transaction.
 * This holds the extracted information from payment notifications.
 */
data class ParsedUpiTransaction(
    val amount: Double,
    val merchant: String,
    val type: String = "debit",
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Extension function to convert ParsedUpiTransaction to Expense domain model.
 * Uses the existing mapping style from the codebase.
 */
fun ParsedUpiTransaction.toExpense(): Expense {
    return Expense(
        amount = this.amount,
        merchant = this.merchant,
        category = "Uncategorized", // Default category, can be categorized later
        date = this.timestamp,
        type = this.type
    )
}
