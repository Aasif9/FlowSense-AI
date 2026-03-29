package com.asif.flowsenseai.ui.model

/**
 * UI state for the Transaction Detail screen.
 */
data class TransactionDetailUiState(
    val id: Long = 0L,
    val merchantName: String = "",
    val amount: Double = 0.0,
    val isDebit: Boolean = true,
    val category: String = "",
    val dateDisplay: String = "",
    val paymentMethod: String = "",
    val notes: String = "",
    val isAutoTracked: Boolean = false,
    val attachmentName: String = "",
    val attachmentSize: String = "",
    val hasAttachment: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * One-shot events for Transaction Detail screen.
 */
sealed class TransactionDetailEvent {
    object NavigateBack : TransactionDetailEvent()
    data class NavigateToEdit(val id: Long) : TransactionDetailEvent()
    object DeleteSuccess : TransactionDetailEvent()
    data class DeleteError(val message: String) : TransactionDetailEvent()
}
