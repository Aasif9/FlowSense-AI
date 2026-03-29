package com.asif.flowsenseai.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asif.flowsenseai.R
import com.asif.flowsenseai.ui.model.SettingsUiState
import com.asif.flowsenseai.ui.screen.components.FlowSenseBottomNav
import com.asif.flowsenseai.ui.theme.*

// ─── Static data model for settings items ─────────────────────────────────────

data class SettingsItem(
    val iconResId: Int,
    val title: String,
    val subtitle: String = "",
    val type: SettingsItemType = SettingsItemType.NAVIGATION
)

enum class SettingsItemType {
    NAVIGATION,   // shows chevron arrow
    TOGGLE,       // shows Switch
    VALUE,        // shows text value + dropdown
    DESTRUCTIVE   // red logout style
}

// ─── Screen (stateless — receives data + callbacks) ─────────────────────────--

@Composable
fun SettingsScreen(
    uiState: SettingsUiState = SettingsUiState(),
    selectedNavIndex: Int = 3,
    onNavItemSelected: (Int) -> Unit = {},
    onEditProfileClicked: () -> Unit = {},
    onFaceIdToggled: (Boolean) -> Unit = {},
    onDarkModeToggled: (Boolean) -> Unit = {},
    onLogOutClicked: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SettingsTopBar()
        },
        bottomBar = {
            FlowSenseBottomNav(
                selectedIndex = selectedNavIndex,
                onItemSelected = onNavItemSelected
            )
        }
    ) { padding ->
        SettingsContent(
            uiState = uiState,
            onEditProfileClicked = onEditProfileClicked,
            onFaceIdToggled = onFaceIdToggled,
            onDarkModeToggled = onDarkModeToggled,
            onLogOutClicked = onLogOutClicked,
            modifier = Modifier.padding(padding)
        )
    }
}

// ─── Top bar ─────────────────────────────────────────────────────────────────-

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar() {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_category_food),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "FlowSense AI",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = ManropeFontFamily
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

// ─── Main scrollable content ─────────────────────────────────────────────────-

@Composable
fun SettingsContent(
    uiState: SettingsUiState,
    onEditProfileClicked: () -> Unit,
    onFaceIdToggled: (Boolean) -> Unit,
    onDarkModeToggled: (Boolean) -> Unit,
    onLogOutClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 8.dp,
            bottom = 120.dp
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Profile card
        item {
            ProfileHeaderCard(
                userName = uiState.userName,
                userEmail = uiState.userEmail,
                onEditClicked = onEditProfileClicked
            )
        }

        // Account Settings section
        item {
            SettingsSectionLabel(text = "ACCOUNT SETTINGS")
            Spacer(Modifier.height(10.dp))
            AccountSettingsGroup(
                linkedBankCount = uiState.linkedBankCount,
                isFaceIdEnabled = uiState.isFaceIdEnabled,
                selectedCurrency = uiState.selectedCurrency,
                onFaceIdToggled = onFaceIdToggled
            )
        }

        // App Preferences section
        item {
            SettingsSectionLabel(text = "APP PREFERENCES")
            Spacer(Modifier.height(10.dp))
            AppPreferencesGroup(
                isDarkModeEnabled = uiState.isDarkModeEnabled,
                onDarkModeToggled = onDarkModeToggled
            )
        }

        // Support & About section
        item {
            SettingsSectionLabel(text = "SUPPORT & ABOUT")
            Spacer(Modifier.height(10.dp))
            SupportAboutGroup(appVersion = uiState.appVersion)
        }

        // Log Out button
        item {
            LogOutButton(onClick = onLogOutClicked)
        }
    }
}

// ─── Profile header card ─────────────────────────────────────────────────────-

@Composable
fun ProfileHeaderCard(
    userName: String,
    userEmail: String,
    onEditClicked: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceContainerLowest,
        shadowElevation = 2.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar placeholder with online indicator
            Box(modifier = Modifier.size(56.dp)) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_category_food),
                        contentDescription = "Avatar",
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                // Online indicator dot
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Secondary)
                        .align(Alignment.BottomEnd)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = ManropeFontFamily
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = userEmail,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            }

            // Edit Profile button
            Surface(
                onClick = onEditClicked,
                shape = CircleShape,
                color = SurfaceContainerHigh
            ) {
                Text(
                    text = "Edit Profile",
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ─── Section label ─────────────────────────────────────────────────────────----

@Composable
fun SettingsSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            fontFamily = ManropeFontFamily
        ),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
    )
}

// ─── Account Settings group ─────────────────────────────────────────────────--

@Composable
fun AccountSettingsGroup(
    linkedBankCount: Int,
    isFaceIdEnabled: Boolean,
    selectedCurrency: String,
    onFaceIdToggled: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceContainerLow)
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        // Linked bank accounts
        SettingsNavItem(
            iconResId = R.drawable.ic_category_food,
            iconBg = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            iconTint = MaterialTheme.colorScheme.primary,
            title = "Linked Bank Accounts",
            subtitle = "$linkedBankCount Connected"
        )

        // Edit profile details
        SettingsNavItem(
            iconResId = R.drawable.ic_category_food,
            iconBg = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            iconTint = MaterialTheme.colorScheme.primary,
            title = "Edit Profile Details"
        )

        // Security (FaceID) — has toggle
        SettingsToggleItem(
            iconResId = R.drawable.ic_category_food,
            iconBg = Tertiary.copy(alpha = 0.1f),
            iconTint = Tertiary,
            title = "Security (FaceID)",
            isEnabled = isFaceIdEnabled,
            onToggled = onFaceIdToggled
        )

        // Currency — has value text
        SettingsValueItem(
            iconResId = R.drawable.ic_category_food,
            iconBg = SecondaryContainer.copy(alpha = 0.3f),
            iconTint = Secondary,
            title = "Currency",
            value = selectedCurrency
        )
    }
}

// ─── App Preferences group ─────────────────────────────────────────────────---

@Composable
fun AppPreferencesGroup(
    isDarkModeEnabled: Boolean,
    onDarkModeToggled: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Dark mode toggle
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = SurfaceContainerLow
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_category_food),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(Modifier.width(14.dp))
                Text(
                    text = "Dark Mode",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = isDarkModeEnabled,
                    onCheckedChange = onDarkModeToggled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                )
            }
        }

        // Notifications
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = SurfaceContainerLow
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_category_food),
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(Modifier.width(14.dp))
                Text(
                    text = "Notifications",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    painter = painterResource(R.drawable.ic_category_food),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }

        // Export Financial Data — gradient CTA card
        ExportDataCard()
    }
}

@Composable
fun ExportDataCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(listOf(Primary, PrimaryContainer))
            )
            .clickable { /* static */ }
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_category_food),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = Color.White
            )
            Spacer(Modifier.width(14.dp))
            Text(
                text = "Export Financial Data",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = Color.White
            )
            // CSV/PDF badge
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "CSV / PDF",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}

// ─── Support & About group ─────────────────────────────────────────-----------

@Composable
fun SupportAboutGroup(appVersion: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = SurfaceContainerLowest,
        shadowElevation = 1.dp
    ) {
        Column {
            // Help Center
            SupportItem(
                iconResId = R.drawable.ic_category_food,
                title = "Help Center",
                showDivider = true
            )

            // Privacy Policy
            SupportItem(
                iconResId = R.drawable.ic_category_food,
                title = "Privacy Policy",
                showDivider = true
            )

            // App Version — no chevron, shows version text
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceContainerLow.copy(alpha = 0.3f))
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_category_food),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                }
                Spacer(Modifier.width(14.dp))
                Text(
                    text = "App Version",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = appVersion,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
private fun SupportItem(
    iconResId: Int,
    title: String,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            }
            Spacer(Modifier.width(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                color = OutlineVariant.copy(alpha = 0.15f),
                thickness = 1.dp
            )
        }
    }
}

// ─── Log Out button ─────────────────────────────────────────────────────────--

@Composable
fun LogOutButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = DebitRedLight
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_category_food),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = DebitRed
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Log Out",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = ManropeFontFamily
                ),
                color = DebitRed
            )
        }
    }
}

// ─── Reusable settings row types ─────────────────────────────────────────-----

@Composable
fun SettingsNavItem(
    iconResId: Int,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String = ""
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
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = iconTint
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
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
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

@Composable
fun SettingsToggleItem(
    iconResId: Int,
    iconBg: Color,
    iconTint: Color,
    title: String,
    isEnabled: Boolean,
    onToggled: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceContainerLowest
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = iconTint
                )
            }
            Spacer(Modifier.width(14.dp))
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )
            )
        }
    }
}

@Composable
fun SettingsValueItem(
    iconResId: Int,
    iconBg: Color,
    iconTint: Color,
    title: String,
    value: String
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
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = iconTint
                )
            }
            Spacer(Modifier.width(14.dp))
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    painter = painterResource(R.drawable.ic_category_food),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}

// ─── Preview ─────────────────────────────────────────────────────────---------

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SettingsScreenPreview() {
    FlowSenseAITheme {
        SettingsScreen(
            uiState = SettingsUiState(),
            onNavItemSelected = {},
            onEditProfileClicked = {},
            onFaceIdToggled = {},
            onDarkModeToggled = {},
            onLogOutClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileHeaderCardPreview() {
    FlowSenseAITheme {
        ProfileHeaderCard(
            userName = "Asif Ali",
            userEmail = "asifali@premium.com",
            onEditClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExportDataCardPreview() {
    FlowSenseAITheme {
        ExportDataCard()
    }
}

@Preview(showBackground = true)
@Composable
private fun LogOutButtonPreview() {
    FlowSenseAITheme {
        LogOutButton(onClick = {})
    }
}
