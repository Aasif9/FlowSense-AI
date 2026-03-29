package com.asif.flowsenseai.ui.screen

import android.app.DatePickerDialog
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asif.flowsenseai.R
import com.asif.flowsenseai.ui.model.AddTransactionEvent
import com.asif.flowsenseai.ui.model.AddTransactionUiState
import com.asif.flowsenseai.ui.theme.*
import com.asif.flowsenseai.ui.viewmodel.AddTransactionViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar

// ─── Income-specific category options ────────────────────────────────────────

private val INCOME_CATEGORIES = listOf(
    "Salary", "Freelance", "Business", "Investment",
    "Gift", "Rental Income", "Refund", "Other"
)

private val EXPENSE_CATEGORIES = listOf(
    "Food & Dining", "Transport", "Shopping", "Entertainment",
    "Utilities", "Health", "Education", "Other"
)

// ─── Screen entry point ───────────────────────────────────────────────────────

/**
 * AddIncomeScreen — opens directly with Income tab selected.
 * Reuses AddTransactionViewModel; just passes startAsIncome = true.
 */
@Composable
fun AddIncomeScreen(
    viewModel: AddTransactionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    // Force income mode on entry
    LaunchedEffect(Unit) {
        viewModel.onTypeToggled(isExpense = false)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is AddTransactionEvent.SaveSuccess -> onNavigateBack()
                is AddTransactionEvent.NavigateBack -> onNavigateBack()
                else -> Unit
            }
        }
    }

    AddTransactionFullScreen(
        uiState = uiState,
        onClose = onNavigateBack,
        onConfirm = viewModel::onSaveClicked,
        onTypeToggled = viewModel::onTypeToggled,
        onAmountChanged = viewModel::onAmountChanged,
        onCategorySelected = viewModel::onCategorySelected,
        onDateChanged = viewModel::onDateChanged,
        onNotesChanged = viewModel::onNotesChanged,
        onAttachProof = viewModel::onAttachReceipt
    )
}

// ─── Full screen composable (Income design variant) ─────────────────────────--

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionFullScreen(
    uiState: AddTransactionUiState,
    onClose: () -> Unit,
    onConfirm: () -> Unit,
    onTypeToggled: (Boolean) -> Unit,
    onAmountChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onDateChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onAttachProof: () -> Unit
) {
    val isIncome = !uiState.isExpense
    val accentColor = if (isIncome) Secondary else MaterialTheme.colorScheme.primary

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Close / Title / Check style top bar (matches screenshot)
            Surface(
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "Add Transaction",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = ManropeFontFamily
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(
                        onClick = onConfirm,
                        enabled = uiState.canSave
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Save",
                            tint = if (uiState.canSave)
                                accentColor
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 40.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            // Expense / Income toggle — Income selected (green)
            IncomeExpenseToggle(
                isExpense = uiState.isExpense,
                onToggle = onTypeToggled
            )

            Spacer(Modifier.height(32.dp))

            // Amount hero — label changes based on type
            IncomeAmountSection(
                label = if (isIncome) "AMOUNT RECEIVED" else "AMOUNT",
                amountText = uiState.amountText,
                accentColor = accentColor,
                onAmountChanged = onAmountChanged
            )

            Spacer(Modifier.height(40.dp))

            // Form fields
            val categories = if (isIncome) INCOME_CATEGORIES else EXPENSE_CATEGORIES

            IncomeCategoryField(
                label = "CATEGORY",
                selectedCategory = uiState.selectedCategory,
                placeholder = if (isIncome) "Select Income Category" else "Select Category",
                categories = categories,
                accentColor = accentColor,
                onCategorySelected = onCategorySelected
            )

            Spacer(Modifier.height(20.dp))

            IncomeDateField(
                selectedDate = uiState.selectedDate,
                accentColor = accentColor,
                onDateChanged = onDateChanged
            )

            Spacer(Modifier.height(20.dp))

            IncomeAttachProofField(
                hasAttachment = uiState.hasAttachment,
                accentColor = accentColor,
                onClick = onAttachProof
            )

            Spacer(Modifier.height(20.dp))

            IncomeNotesField(
                notes = uiState.notes,
                accentColor = accentColor,
                onNotesChanged = onNotesChanged
            )

            // Attachment preview card — shown when file is attached
            if (uiState.hasAttachment) {
                Spacer(Modifier.height(16.dp))
                AttachmentPreviewCard(
                    fileName = "INV-2023-084.pdf",
                    fileSize = "1.2 MB • Attached",
                    onDelete = { /* viewModel.onRemoveAttachment() */ }
                )
            }

            Spacer(Modifier.height(32.dp))

            // Save button — green for income, gradient for expense
            IncomeSaveButton(
                isIncome = isIncome,
                canSave = uiState.canSave,
                isSaving = uiState.isSaving,
                onClick = onConfirm
            )
        }
    }
}

// ─── Toggle: green for income ─────────────────────────────────────────────────

@Composable
fun IncomeExpenseToggle(
    isExpense: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceContainerHigh)
            .padding(6.dp)
    ) {
        Row {
            // Expense tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isExpense) MaterialTheme.colorScheme.primary
                        else Color.Transparent
                    )
                    .clickable { onToggle(true) }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Expense",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = if (isExpense) Color.White
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Income tab — green when selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (!isExpense) Secondary
                        else Color.Transparent
                    )
                    .clickable { onToggle(false) }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Income",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = if (!isExpense) Color.White
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

// ─── Amount hero (income variant — green prefix) ──────────────────────────────

@Composable
fun IncomeAmountSection(
    label: String,
    amountText: String,
    accentColor: Color,
    onAmountChanged: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Label — "AMOUNT RECEIVED" for income
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
        )

        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Currency symbol — accentColor (green for income)
            Text(
                text = "₹",
                style = TextStyle(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor,
                    fontFamily = ManropeFontFamily
                )
            )
            Spacer(Modifier.width(6.dp))
            BasicTextField(
                value = amountText,
                onValueChange = onAmountChanged,
                textStyle = TextStyle(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start,
                    fontFamily = ManropeFontFamily
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                cursorBrush = SolidColor(accentColor),
                singleLine = true,
                modifier = Modifier.widthIn(min = 80.dp, max = 260.dp)
            )
        }
    }
}

// ─── Income field label ───────────────────────────────────────────────────────

@Composable
private fun IncomeFieldLabel(text: String, accentColor: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        ),
        color = accentColor
    )
}

// ─── Category field (income variant — green icon box) ────────────────────────

@Composable
fun IncomeCategoryField(
    label: String,
    selectedCategory: String,
    placeholder: String,
    categories: List<String>,
    accentColor: Color,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        IncomeFieldLabel(text = label, accentColor = accentColor)
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceContainerHighest)
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icon box — green background for income
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (accentColor == Secondary) SecondaryContainer
                            else SurfaceContainerHigh
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_category_food),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = accentColor
                    )
                }

                Spacer(Modifier.width(14.dp))

                Text(
                    text = selectedCategory.ifBlank { placeholder },
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = if (selectedCategory.isBlank())
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    else
                        MaterialTheme.colorScheme.onSurface
                )

                Icon(
                    painter = painterResource(R.drawable.ic_category_food),
                    contentDescription = "Expand",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = {
                            onCategorySelected(category)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

// ─── Date field ───────────────────────────────────────────────────────────────

@Composable
fun IncomeDateField(
    selectedDate: String,
    accentColor: Color,
    onDateChanged: (String) -> Unit
) {
    val context = LocalContext.current

    Column {
        IncomeFieldLabel(text = "TRANSACTION DATE", accentColor = accentColor)
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceContainerHighest)
                .clickable {
                    val cal = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            val months = listOf(
                                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                            )
                            onDateChanged("${months[month]} $day, $year")
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_category_food),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(Modifier.width(14.dp))
                Text(
                    text = selectedDate.ifBlank { "Select date" },
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// ─── Attach proof field ───────────────────────────────────────────────────────

@Composable
fun IncomeAttachProofField(
    hasAttachment: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Column {
        IncomeFieldLabel(text = "ATTACH PROOF", accentColor = accentColor)
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceContainerHighest)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_category_food),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(Modifier.width(14.dp))
                Text(
                    text = if (hasAttachment) "File attached ✓" else "Add Receipt/Invoice",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = if (hasAttachment)
                        accentColor
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                )
            }
        }
    }
}

// ─── Notes field ──────────────────────────────────────────────────────────────

@Composable
fun IncomeNotesField(
    notes: String,
    accentColor: Color,
    onNotesChanged: (String) -> Unit
) {
    Column {
        IncomeFieldLabel(text = "NOTES", accentColor = accentColor)
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceContainerHighest)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    painter = painterResource(R.drawable.ic_category_food),
                    contentDescription = null,
                    modifier = Modifier
                        .size(22.dp)
                        .padding(top = 2.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                )
                Spacer(Modifier.width(14.dp))
                BasicTextField(
                    value = notes,
                    onValueChange = onNotesChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 56.dp),
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = InterFontFamily,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(accentColor),
                    decorationBox = { inner ->
                        if (notes.isEmpty()) {
                            Text(
                                text = "Add a description for this income...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                            )
                        }
                        inner()
                    }
                )
            }
        }
    }
}

// ─── Attachment preview card ──────────────────────────────────────────────────

@Composable
fun AttachmentPreviewCard(
    fileName: String,
    fileSize: String,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceContainerLowest,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // File thumbnail
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(OnSurface.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_category_food),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = Color.White
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fileName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = fileSize,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                )
            }

            // Delete button — red
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_category_food),
                    contentDescription = "Remove attachment",
                    modifier = Modifier.size(18.dp),
                    tint = DebitRed
                )
            }
        }
    }
}

// ─── Save button (green for income) ──────────────────────────────────────────

@Composable
fun IncomeSaveButton(
    isIncome: Boolean,
    canSave: Boolean,
    isSaving: Boolean,
    onClick: () -> Unit
) {
    val bgColor = when {
        !canSave -> SurfaceContainerHigh
        isIncome -> Secondary
        else -> null // use gradient for expense
    }

    Button(
        onClick = onClick,
        enabled = canSave && !isSaving,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = SurfaceContainerHigh
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    when {
                        !canSave -> SurfaceContainerHigh
                        isIncome -> Secondary // solid green for income
                        else -> Color.Transparent // gradient handled below
                    }
                )
                .then(
                    if (canSave && !isIncome)
                        Modifier.background(
                            Brush.linearGradient(listOf(Primary, PrimaryContainer))
                        )
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_category_food),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (canSave) Color.White
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                    )
                    Text(
                        text = "Save Transaction",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = ManropeFontFamily
                        ),
                        color = if (canSave) Color.White
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                    )
                }
            }
        }
    }
}

// ─── Preview ─────────────────────────────────────────────────────────────────-

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddIncomeScreenPreview() {
    FlowSenseAITheme {
        AddTransactionFullScreen(
            uiState = AddTransactionUiState(
                isExpense = false,
                amountText = "0.00",
                selectedDate = "Oct 24, 2023",
                hasAttachment = true
            ),
            onClose = {},
            onConfirm = {},
            onTypeToggled = {},
            onAmountChanged = {},
            onCategorySelected = {},
            onDateChanged = {},
            onNotesChanged = {},
            onAttachProof = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun IncomeTogglePreview() {
    FlowSenseAITheme {
        IncomeExpenseToggle(isExpense = false, onToggle = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun AttachmentCardPreview() {
    FlowSenseAITheme {
        AttachmentPreviewCard(
            fileName = "INV-2023-084.pdf",
            fileSize = "1.2 MB • Attached",
            onDelete = {},
        )
    }
}
