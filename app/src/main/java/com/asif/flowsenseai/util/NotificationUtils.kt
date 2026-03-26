package com.asif.flowsenseai.util

import android.content.Context
import android.provider.Settings
import android.text.TextUtils

/**
 * Utility class for notification-related operations.
 * Provides helper functions to check notification listener permissions.
 */
object NotificationUtils {
    
    /**
     * Check if the notification listener service is enabled.
     * This verifies if the user has granted notification access to our app.
     * 
     * @param context Application context
     * @return true if notification listener is enabled, false otherwise
     */
    fun isNotificationListenerEnabled(context: Context): Boolean {
        try {
            val packageName = context.packageName
            val flat = Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners"
            )
            
            // Check if our package name is in the enabled listeners list
            return !TextUtils.isEmpty(flat) && flat.contains(packageName)
            
        } catch (e: Exception) {
            // In case of any error, assume permission is not granted
            return false
        }
    }
    
    /**
     * Get the component name of our notification listener service.
     * This is used for various notification-related operations.
     * 
     * @param context Application context
     * @return The full component name of our notification listener service
     */
    fun getNotificationListenerComponentName(context: Context): String {
        return "${context.packageName}/com.asif.flowsenseai.data.notification.UpiNotificationListener"
    }
    
    /**
     * Check if we can read notifications from a specific package.
     * This is useful for debugging and testing.
     * 
     * @param context Application context
     * @param packageName Package name to check
     * @return true if we can read notifications from the package
     */
    fun canReadNotificationsFromPackage(context: Context, packageName: String): Boolean {
        // For now, just check if our listener is enabled
        // Later we can add more specific package checking
        return isNotificationListenerEnabled(context)
    }
    
    /**
     * Get a list of popular UPI app package names.
     * This is used to identify UPI payment notifications.
     * 
     * @return Set of UPI app package names
     */
    fun getUpiAppPackages(): Set<String> {
        return setOf(
            "com.google.android.apps.nbu.paisa.user", // Google Pay
            "com.phonepe.app",                       // PhonePe
            "net.one97.paytm",                       // Paytm
            "com.amazon.mShop.android.shopping",     // Amazon Pay (UPI)
            "in.org.npci.upiapp"                     // BHIM UPI
        )
    }
    
    /**
     * Check if a package name belongs to a UPI app.
     * 
     * @param packageName Package name to check
     * @return true if it's a UPI app, false otherwise
     */
    fun isUpiApp(packageName: String): Boolean {
        return getUpiAppPackages().contains(packageName)
    }
    
    /**
     * Get user-friendly app name from package name.
     * 
     * @param packageName Package name of the app
     * @return User-friendly app name
     */
    fun getAppName(packageName: String): String {
        return when (packageName) {
            "com.google.android.apps.nbu.paisa.user" -> "Google Pay"
            "com.phonepe.app" -> "PhonePe"
            "net.one97.paytm" -> "Paytm"
            "com.amazon.mShop.android.shopping" -> "Amazon Pay"
            "in.org.npci.upiapp" -> "BHIM UPI"
            else -> "Unknown App"
        }
    }
}
