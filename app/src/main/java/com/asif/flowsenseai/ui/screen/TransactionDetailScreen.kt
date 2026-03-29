package com.asif.flowsenseai.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asif.flowsenseai.R
import com.asif.flowsenseai.ui.model.TransactionDetailEvent
import com.asif.flowsenseai.ui.model.TransactionDetailUiState
import com.asif.flowsenseai.ui.theme.*
import com.asif.flowsenseai.ui.viewmodel.TransactionDetailViewModel
import kotlinx.coroutines.flow.collectLatest

// ─── Screen entry point ───────────────────────────────────────────────────────

@Composable
fun TransactionDetailScreen(
    viewModel: TransactionDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // One-shot navigation events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is TransactionDetailEvent.NavigateBack -> onNavigateBack()
                is TransactionDetailEvent.NavigateToEdit -> onNavigateToEdit(event.id)
                is TransactionDetailEvent.DeleteSuccess -> onNavigateBack()
                else -> Unit
            }
        }
    }

    TransactionDetailScaffold(
        uiState = uiState,
        onBack = viewModel::onBackClicked,
        onEdit = viewModel::onEditClicked,
        onDelete = viewModel::onDeleteClicked
    )
}

// ─── Scaffold ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionDetailScaffold(
    uiState: TransactionDetailUiState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Transaction Details",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = ManropeFontFamily
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* show options menu */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> DetailLoadingState(Modifier.padding(padding))
            uiState.error != null -> DetailErrorState(uiState.error, Modifier.padding(padding))
            else -> TransactionDetailContent(
                uiState = uiState,
                onEdit = onEdit,
                onDelete = { showDeleteDialog = true },
                modifier = Modifier.padding(padding)
            )
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        DeleteConfirmDialog(
            merchantName = uiState.merchantName,
            onConfirm = {
                showDeleteDialog = false
                onDelete()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

// ─── Main detail content ──────────────────────────────────────────────────────

@Composable
fun TransactionDetailContent(
    uiState: TransactionDetailUiState,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 40.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        // Hero amount card
        TransactionAmountHeroCard(uiState = uiState)

        Spacer(Modifier.height(24.dp))

        // Metadata detail cards (Date, Payment method)
        TransactionMetaCard(
            iconResId = R.drawable.ic_category_food,
            label = "DATE",
            value = uiState.dateDisplay
        )

        Spacer(Modifier.height(12.dp))

        TransactionMetaCard(
            iconResId = R.drawable.ic_category_food,
            label = "PAYMENT METHOD",
            value = uiState.paymentMethod.ifBlank { "UPI" }
        )

        Spacer(Modifier.height(20.dp))

        // Note card — only shown when notes exist
        if (uiState.notes.isNotBlank()) {
            TransactionNoteCard(note = uiState.notes)
            Spacer(Modifier.height(20.dp))
        }

        // Attachment preview — only shown when file is attached
        if (uiState.hasAttachment) {
            AttachmentPreviewCard(
                fileName = uiState.attachmentName.ifBlank { "receipt.pdf" },
                fileSize = uiState.attachmentSize.ifBlank { "Attached" },
                onDelete = { /* no-op in detail view */ }
            )
            Spacer(Modifier.height(20.dp))
        }

        Spacer(Modifier.height(8.dp))

        // Edit + Delete action row
        TransactionDetailActions(
            onEdit = onEdit,
            onDelete = onDelete
        )
    }
}

// ─── Hero amount card ─────────────────────────────────────────────────────────

@Composable
fun TransactionAmountHeroCard(uiState: TransactionDetailUiState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceContainerLow)
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Auto-tracked badge — top right
            if (uiState.isAutoTracked) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    AutoTrackedBadge()
                }
            }

            // Amount — red for debit, green for credit
            val amountColor = if (uiState.isDebit) DebitRed else CreditGreen
            val amountPrefix = if (uiState.isDebit) "-" else "+"

            Text(
                text = "$amountPrefix₹${formatAmount(uiState.amount)}",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = ManropeFontFamily,
                    fontSize = 48.sp
                ),
                color = amountColor
            )

            Spacer(Modifier.height(10.dp))

            // Merchant name
            Text(
                text = uiState.merchantName,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = ManropeFontFamily
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(6.dp))

            // Category
            Text(
                text = uiState.category,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
        }
    }
}

// ─── Auto-tracked badge ───────────────────────────────────────────────────────

@Composable
fun AutoTrackedBadge() {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(TertiaryFixed)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // Star sparkle icon placeholder — replace with your actual icon
            Text(
                text = "✦",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = OnTertiaryFixed
            )
            Text(
                text = "Auto-Tracked",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.3.sp
                ),
                color = OnTertiaryFixed
            )
        }
    }
}

// ─── Metadata card (Date, Payment method) ────────────────────────────────────

@Composable
fun TransactionMetaCard(
    iconResId: Int,
    label: String,
    value: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceContainerLowest,
        shadowElevation = 1.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon box — soft blue background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconResId),
                    contentDescription = label,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// ─── Note card ────────────────────────────────────────────────────────────────

@Composable
fun TransactionNoteCard(note: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceContainerLowest,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_category_food),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Note",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = ManropeFontFamily
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(12.dp))

            // Italic note text — matching design screenshot
            Text(
                text = "\"$note\"",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontStyle = FontStyle.Italic,
                    lineHeight = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )
        }
    }
}

// ─── Edit + Delete action buttons ────────────────────────────────────────────

@Composable
fun TransactionDetailActions(
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Edit — blue tinted
        Button(
            onClick = onEdit,
            modifier = Modifier
                .weight(1f)
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SurfaceContainerHigh,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_category_food),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Edit",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        // Delete — red tinted
        Button(
            onClick = onDelete,
            modifier = Modifier
                .weight(1f)
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DebitRedLight,
                contentColor = DebitRed
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_category_food),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Delete",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

// ─── Delete confirmation dialog ───────────────────────────────────────────────

@Composable
fun DeleteConfirmDialog(
    merchantName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceContainerLowest,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = "Delete Transaction",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = ManropeFontFamily
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Text(
                text = "Are you sure you want to delete the transaction for $merchantName? This action cannot be undone.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = DebitRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}

// ─── State views ──────────────────────────────────────────────────────────────

@Composable
private fun DetailLoadingState(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun DetailErrorState(message: String, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TransactionDetailPreview() {
    FlowSenseAITheme {
        TransactionDetailContent(
            uiState = TransactionDetailUiState(
                merchantName = "Amazon",
                amount = 1200.0,
                isDebit = true,
                category = "Shopping & Personal",
                dateDisplay = "Today, 09:20 AM",
                paymentMethod = "ICICI Bank Debit Card",
                notes = "New office chair for the home studio. Needed better lumbar support for long design sessions.",
                isAutoTracked = true,
                hasAttachment = false
            ),
            onEdit = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AutoTrackedBadgePreview() {
    FlowSenseAITheme {
        Box(Modifier.padding(16.dp)) {
            AutoTrackedBadge()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionMetaCardPreview() {
    FlowSenseAITheme {
        TransactionMetaCard(
            iconResId = R.drawable.ic_category_food,
            label = "DATE",
            value = "Today, 09:20 AM"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DeleteDialogPreview() {
    FlowSenseAITheme {
        DeleteConfirmDialog(
            merchantName = "Amazon",
            onConfirm = {},
            onDismiss = {}
        )
    }
}

/**
 * Central amount formatter. Called from all components.
 * Keep in one place so format changes apply everywhere.
 */
private fun formatAmount(amount: Double): String {
    return when {
        amount >= 100_000 -> "%.1fL".format(amount / 100_000)
        amount >= 1_000 -> "%,.0f".format(amount)
        else -> "%.0f".format(amount)
    }
}
