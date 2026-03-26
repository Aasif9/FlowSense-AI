package com.asif.flowsenseai.data.notification

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.asif.flowsenseai.domain.repository.ExpenseRepository
import com.asif.flowsenseai.util.UpiParser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Listens to all notifications and captures any that look like UPI/bank payments.
 * Instead of filtering by package name (which breaks bank SMS), we filter by
 * notification CONTENT — if it has a rupee amount + payment keywords, we save it.
 */
@AndroidEntryPoint
class UpiNotificationListener : NotificationListenerService() {

    companion object {
        private const val TAG = "UpiNotificationListener"

        // Apps whose notifications we should IGNORE entirely (non-financial noise)
        private val IGNORED_PACKAGES = setOf(
            "com.whatsapp",
            "com.instagram.android",
            "com.facebook.katana",
            "com.twitter.android",
            "com.google.android.youtube",
            "com.netflix.mediaclient"
        )
    }

    // Hilt injection — this is why we needed @AndroidEntryPoint
    @Inject
    lateinit var repository: ExpenseRepository

    // Coroutine scope tied to the service lifecycle
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.i(TAG, "✅ Notification Listener connected — watching for UPI payments")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.w(TAG, "⚠️ Notification Listener disconnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn ?: return

        val packageName = sbn.packageName ?: return

        // Skip noise apps immediately
        if (IGNORED_PACKAGES.contains(packageName)) return

        val extras = sbn.notification?.extras ?: return
        val title = extras.getCharSequence("android.title")?.toString() ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val bigText = extras.getCharSequence("android.bigText")?.toString() ?: ""

        // Use bigText if available (more complete), else fall back to text
        val body = bigText.ifBlank { text }

        Log.d(TAG, "--- Notification from $packageName ---")
        Log.d(TAG, "Title: $title | Body: $body")

        // Safety check for Hilt injection
        if (!::repository.isInitialized) {
            Log.w(TAG, "Repository not ready yet, skipping notification")
            return
        }

        // Hand off to unified parser — content-based detection, not package-based
        val parsed = UpiParser.parse(title, body)

        if (parsed != null) {
            saveToDatabase(parsed, packageName)
        } else {
            Log.d(TAG, "Not a payment notification from $packageName, skipping.")
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Not needed
    }

    /**
     * Saves the parsed transaction to the Room database via repository.
     * Runs on IO dispatcher — never blocks the main thread.
     */
    private fun saveToDatabase(parsed: ParsedUpiTransaction, source: String) {
        serviceScope.launch {
            try {
                Log.i(TAG, "💾 Saving: ₹${parsed.amount} to ${parsed.merchant} (source: $source)")
                repository.insertFromNotification(parsed)
                Log.i(TAG, "✅ Saved successfully!")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to save transaction: ${e.message}", e)
            }
        }
    }
}
