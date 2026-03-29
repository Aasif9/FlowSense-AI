package com.asif.flowsenseai.ui.screen

import android.app.DatePickerDialog
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
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

// ─── Static category data (no ViewModel logic) ────────────────────────────────

private val EXPENSE_CATEGORIES = listOf(
    "Food & Dining", "Transport", "Shopping", "Entertainment",
    "Utilities", "Health", "Education", "Other"
)

private val INCOME_CATEGORIES = listOf(
    "Salary", "Freelance", "Investment", "Gift", "Refund", "Other"
)

// ─── Screen entry point ───────────────────────────────────────────────────────

@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToIncome: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Debug logging for state changes
    LaunchedEffect(uiState.isExpense) {
        println("DEBUG: AddTransactionScreen - isExpense changed to: ${uiState.isExpense}")
    }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is AddTransactionEvent.SaveSuccess -> onNavigateBack()
                is AddTransactionEvent.NavigateBack -> onNavigateBack()
                else -> Unit
            }
        }
    }

    AddTransactionScaffold(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onTypeToggled = viewModel::onTypeToggled,
        onAmountChanged = viewModel::onAmountChanged,
        onCategorySelected = viewModel::onCategorySelected,
        onDateChanged = viewModel::onDateChanged,
        onNotesChanged = viewModel::onNotesChanged,
        onAttachReceipt = viewModel::onAttachReceipt,
        onSaveClicked = viewModel::onSaveClicked
    )
}

// ─── Scaffold ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTransactionScaffold(
    uiState: AddTransactionUiState,
    onNavigateBack: () -> Unit,
    onTypeToggled: (Boolean) -> Unit,
    onAmountChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onDateChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onAttachReceipt: () -> Unit,
    onSaveClicked: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Transaction",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        AddTransactionContent(
            uiState = uiState,
            onTypeToggled = onTypeToggled,
            onAmountChanged = onAmountChanged,
            onCategorySelected = onCategorySelected,
            onDateChanged = onDateChanged,
            onNotesChanged = onNotesChanged,
            onAttachReceipt = onAttachReceipt,
            onSaveClicked = onSaveClicked,
            modifier = Modifier.padding(padding)
        )
    }
}

// ─── Main form content ────────────────────────────────────────────────────────

@Composable
fun AddTransactionContent(
    uiState: AddTransactionUiState,
    onTypeToggled: (Boolean) -> Unit,
    onAmountChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onDateChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onAttachReceipt: () -> Unit,
    onSaveClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        // Expense / Income toggle
        AddTransactionToggle(
            isExpense = uiState.isExpense,
            onToggle = onTypeToggled
        )

        Spacer(Modifier.height(32.dp))

        // Amount hero section
        AmountSection(
            amountText = uiState.amountText,
            isExpense = uiState.isExpense,
            onAmountChanged = onAmountChanged
        )

        Spacer(Modifier.height(40.dp))

        // Form fields
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Category
            val categories = if (uiState.isExpense) EXPENSE_CATEGORIES else INCOME_CATEGORIES
            CategoryField(
                selectedCategory = uiState.selectedCategory,
                categories = categories,
                onCategorySelected = onCategorySelected
            )

            // Date
            DateField(
                selectedDate = uiState.selectedDate,
                onDateChanged = onDateChanged
            )

            // Notes
            NotesField(
                notes = uiState.notes,
                draftSaved = uiState.draftSaved,
                onNotesChanged = onNotesChanged
            )

            // Attach receipt
            AttachReceiptSection(
                hasAttachment = uiState.hasAttachment,
                onClick = onAttachReceipt
            )
        }

        Spacer(Modifier.height(32.dp))

        // Save button
        SaveButton(
            canSave = uiState.canSave,
            isSaving = uiState.isSaving,
            onClick = onSaveClicked
        )
    }
}

// ─── Toggle: Expense / Income ─────────────────────────────────────────────────

@Composable
fun AddTransactionToggle(
    isExpense: Boolean,
    onToggle: (Boolean) -> Unit
) {
    println("DEBUG: AddTransactionToggle - Current isExpense: $isExpense")
    
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
                    .background(if (isExpense) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { 
                        println("DEBUG: Expense tab clicked")
                        onToggle(true) 
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Expense",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = if (isExpense) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            }

            // Income tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (!isExpense) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { 
                        println("DEBUG: Income tab clicked")
                        onToggle(false) 
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Income",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = if (!isExpense) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            }
        }
    }
}

// ─── Amount hero ──────────────────────────────────────────────────────────────

@Composable
fun AmountSection(
    amountText: String,
    isExpense: Boolean = true,
    onAmountChanged: (String) -> Unit
) {
    println("DEBUG: AmountSection - isExpense: $isExpense")
    
    val label = if (isExpense) "AMOUNT" else "AMOUNT RECEIVED"
    val amountColor = if (isExpense) MaterialTheme.colorScheme.primary else Secondary
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )

        Spacer(Modifier.height(12.dp))

        // Currency prefix + large editable field
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "₹",
                style = TextStyle(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = amountColor,
                    fontFamily = ManropeFontFamily
                )
            )
            Spacer(Modifier.width(4.dp))
            BasicTextField(
                value = amountText,
                onValueChange = onAmountChanged,
                textStyle = TextStyle(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    fontFamily = ManropeFontFamily
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                cursorBrush = SolidColor(amountColor),
                singleLine = true,
                modifier = Modifier.widthIn(min = 80.dp, max = 260.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Currency pill
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(TertiaryFixed)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_category_food),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = OnTertiaryFixed
                )
                Text(
                    text = "INR - Indian Rupee",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = OnTertiaryFixed
                )
            }
        }
    }
}

// ─── Category selector ────────────────────────────────────────────────────────

@Composable
fun CategoryField(
    selectedCategory: String,
    categories: List<String>,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        FieldLabel(text = "Category")
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
                Icon(
                    painter = painterResource(R.drawable.ic_category_food),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = selectedCategory.ifBlank { "Select Category" },
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = if (selectedCategory.isBlank())
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    painter = painterResource(R.drawable.ic_category_food), // use expand_more vector
                    contentDescription = "Expand",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
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

// ─── Date picker ──────────────────────────────────────────────────────────────

@Composable
fun DateField(
    selectedDate: String,
    onDateChanged: (String) -> Unit
) {
    val context = LocalContext.current

    Column {
        FieldLabel(text = "Transaction Date")
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
                        {_, year, month, day ->
                            onDateChanged("%02d/%02d/%04d".format(month + 1, day, year))
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
                    painter = painterResource(R.drawable.ic_category_food), // use calendar_today vector
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = selectedDate.ifBlank { "Select date" },
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    painter = painterResource(R.drawable.ic_category_food),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                )
            }
        }
    }
}

// ─── Notes field ──────────────────────────────────────────────────────────────

@Composable
fun NotesField(
    notes: String,
    draftSaved: Boolean,
    onNotesChanged: (String) -> Unit
) {
    Column {
        FieldLabel(text = "Notes")
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceContainerHighest)
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        painter = painterResource(R.drawable.ic_category_food),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp).padding(top = 2.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(12.dp))
                    BasicTextField(
                        value = notes,
                        onValueChange = onNotesChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 80.dp),
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = InterFontFamily,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        decorationBox = { inner ->
                            if (notes.isEmpty()) {
                                Text(
                                    text = "What was this for?",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                                )
                            }
                            inner()
                        }
                    )
                }

                // Draft saved indicator
                AnimatedVisibility(
                    visible = draftSaved,
                    enter = fadeIn(tween(200)) + slideInVertically { it },
                    exit = fadeOut(tween(200))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.background,
                            shadowElevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "DRAFTS SAVED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Secondary)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Attach receipt ───────────────────────────────────────────────────────────

@Composable
fun AttachReceiptSection(
    hasAttachment: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceContainerLowest,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            OutlineVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Receipt icon box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_category_food),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (hasAttachment) "Receipt Attached" else "Attach Receipt",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Auto-scan for easy tracking",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Plus button
            Surface(
                onClick = onClick,
                shape = CircleShape,
                color = if (hasAttachment) Secondary.copy(alpha = 0.1f) else SurfaceContainerHigh,
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = if (hasAttachment) "✓" else "+",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (hasAttachment) Secondary else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// ─── Save button ──────────────────────────────────────────────────────────────

@Composable
fun SaveButton(
    canSave: Boolean,
    isSaving: Boolean,
    onClick: () -> Unit
) {
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
                    brush = if (canSave && !isSaving)
                        Brush.linearGradient(listOf(Primary, PrimaryContainer))
                    else
                        Brush.linearGradient(listOf(SurfaceContainerHigh, SurfaceContainerHigh))
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
                        painter = painterResource(R.drawable.ic_category_food), // use task_alt
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (canSave) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                    )
                    Text(
                        text = "Save Transaction",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = ManropeFontFamily
                        ),
                        color = if (canSave) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                    )
                }
            }
        }
    }
}

// ─── Shared small composables ─────────────────────────────────────────────────

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    )
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddTransactionPreview() {
    FlowSenseAITheme {
        AddTransactionContent(
            uiState = AddTransactionUiState(
                isExpense = true,
                amountText = "0.00",
                selectedDate = "03/24/2026",
                draftSaved = true
            ),
            onTypeToggled = {},
            onAmountChanged = {},
            onCategorySelected = {},
            onDateChanged = {},
            onNotesChanged = {},
            onAttachReceipt = {},
            onSaveClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddTransactionTogglePreview() {
    FlowSenseAITheme {
        AddTransactionToggle(isExpense = true, onToggle = {})
    }
}
