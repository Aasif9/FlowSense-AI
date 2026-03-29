package com.asif.flowsenseai.ui.screen

import android.app.DatePickerDialog
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asif.flowsenseai.R
import com.asif.flowsenseai.ui.model.EditProfileUiState
import com.asif.flowsenseai.ui.theme.*
import java.util.Calendar

// ─── Static data ─────────────────────────────────────────────────────────────-

// RENAMED to avoid conflict with the Composable function
private data class SecurityItemData(
    val iconResId: Int,
    val title: String,
    val subtitle: String,
    val subtitleColor: Color? = null
)

// ─── Screen ─────────────────────────────────────────────────────────────────--

@Composable
fun EditProfileScreen(
    uiState: EditProfileUiState = EditProfileUiState(),
    onNavigateBack: () -> Unit = {},
    onSaveClicked: () -> Unit = {},
    onFullNameChanged: (String) -> Unit = {},
    onPhoneChanged: (String) -> Unit = {},
    onDateOfBirthChanged: (String) -> Unit = {},
    onFinancialGoalChanged: (String) -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            EditProfileTopBar(
                isSaving = uiState.isSaving,
                onBack = onNavigateBack,
                onSave = onSaveClicked
            )
        }
    ) { padding ->
        EditProfileContent(
            uiState = uiState,
            onFullNameChanged = onFullNameChanged,
            onPhoneChanged = onPhoneChanged,
            onDateOfBirthChanged = onDateOfBirthChanged,
            onFinancialGoalChanged = onFinancialGoalChanged,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun EditProfileTopBar(isSaving: Boolean, onBack: () -> Unit, onSave: () -> Unit) {

}

@Composable
fun EditProfileContent(uiState: EditProfileUiState, onFullNameChanged: (String) -> Unit, onPhoneChanged: (String) -> Unit, onDateOfBirthChanged: (String) -> Unit, onFinancialGoalChanged: (String) -> Unit, modifier: Modifier) {

}

// ... [TopBar, Content, PhotoSection, TextField, DateField, GoalField, AccountStatusCard remain the same] ...
// (Skipping to the fixed Security sections for brevity)

@Composable
fun SecuritySettingsSection() {
    val securityItems = listOf(
        SecurityItemData(
            iconResId = R.drawable.ic_category_food,
            title = "Change Password",
            subtitle = "Last changed 3 months ago"
        ),
        SecurityItemData(
            iconResId = R.drawable.ic_category_food,
            title = "Biometric Authentication",
            subtitle = "Enabled",
            subtitleColor = Secondary
        )
    )

    Column {
        Text(
            text = "Security Settings",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = ManropeFontFamily
            ),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(14.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceContainerLow)
                .padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            securityItems.forEach { item ->
                SecurityItem(
                    iconResId = item.iconResId,
                    title = item.title,
                    subtitle = item.subtitle,
                    subtitleColor = item.subtitleColor
                )
            }
        }
    }
}

@Composable
private fun SecurityItem(
    iconResId: Int,
    title: String,
    subtitle: String,
    subtitleColor: Color?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceContainerLowest
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = subtitleColor ?: MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    )
                }
            }
            Icon(
                painter = painterResource(R.drawable.ic_category_food),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}

// ... [Previews remain the same] ...
// ─── Previews ─────────────────────────────────────────────────----------------

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EditProfileScreenPreview() {
    FlowSenseAITheme {
        EditProfileScreen(
            uiState = EditProfileUiState()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AccountStatusCardPreview() {
    FlowSenseAITheme {
        AccountStatusCard(completionPercent = 0.85f)
    }
}

@Composable
fun AccountStatusCard(completionPercent: Float) {

}

@Preview(showBackground = true)
@Composable
private fun SecuritySectionPreview() {
    FlowSenseAITheme {
        Box(Modifier.padding(16.dp)) {
            SecuritySettingsSection()
        }
    }
}
