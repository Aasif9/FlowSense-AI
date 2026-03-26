package com.asif.flowsenseai.domain.model

import com.asif.flowsenseai.data.local.ExpenseEntity

/**
 * Simple data class representing an expense in the domain layer.
 * This is a clean model that the UI layer will use.
 * It maps from the ExpenseEntity but keeps the structure simple.
 */
data class Expense(
    val id: Long = 0, // Unique identifier for the expense
    val amount: Double, // Amount spent
    val merchant: String, // Where the expense was made
    val category: String, // Category of expense
    val date: Long, // Timestamp when expense was made
    val type: String = "debit" // Type of transaction
)

/**
 * Extension function to convert ExpenseEntity to Expense (domain model).
 * This is useful when we get data from the database and want to use it in the UI.
 */
fun ExpenseEntity.toExpense(): Expense {
    return Expense(
        id = this.id,
        amount = this.amount,
        merchant = this.merchant,
        category = this.category,
        date = this.date,
        type = this.type
    )
}

/**
 * Extension function to convert Expense (domain model) to ExpenseEntity.
 * This is useful when we want to save data from the UI to the database.
 */
fun Expense.toExpenseEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = this.id,
        amount = this.amount,
        merchant = this.merchant,
        category = this.category,
        date = this.date,
        type = this.type
    )
}
