package com.asif.flowsenseai.ui.model

/**
 * UI state for Settings screen — all preferences.
 * ViewModel will own this; composables only read it.
 */
data class SettingsUiState(
    val userName: String = "Alex Carter",
    val userEmail: String = "alex.carter@premium.com",
    val userAvatarUrl: String = "",
    val isFaceIdEnabled: Boolean = true,
    val isDarkModeEnabled: Boolean = false,
    val selectedCurrency: String = "INR (₹)",
    val linkedBankCount: Int = 2,
    val appVersion: String = "v2.4.0 (Gold)"
)

/**
 * UI state for Edit Profile screen.
 */
data class EditProfileUiState(
    val fullName: String = "Alexander Sterling",
    val email: String = "a.sterling@indigofinance.com",
    val phoneNumber: String = "+1 (555) 892-4410",
    val dateOfBirth: String = "05/14/1988",
    val financialGoal: String = "Achieve financial independence by 45 through diversified portfolio growth and disciplined real estate investments.",
    val profileCompletionPercent: Float = 0.85f,
    val isSaving: Boolean = false
)
