package com.asif.flowsenseai.data.notification

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

/**
 * Notification Listener Service for detecting UPI payment notifications.
 * This service runs in the background and listens to all notifications.
 * We filter for UPI apps like Google Pay, PhonePe, and Paytm.
 */
class UpiNotificationListener : NotificationListenerService() {
    
    companion object {
        private const val TAG = "UpiNotificationListener"
        
        // Package names of popular UPI apps in India
        private val UPI_APPS = setOf(
            "com.google.android.apps.nbu.paisa.user", // Google Pay
            "com.phonepe.app",                       // PhonePe
            "net.one97.paytm",                       // Paytm
            "com.amazon.mShop.android.shopping",     // Amazon Pay (UPI)
            "in.org.npci.upiapp",                    // BHIM UPI
            "com.csam.cbss.paytm",                   // Paytm Payments Bank
            "com.yono"                               // YONO SBI
        )
    }
    
    /**
     * Called when a new notification is posted.
     * This is where we'll detect UPI payment notifications.
     */
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        
        sbn?.let { notification ->
            val packageName = notification.packageName
            val extras = notification.notification.extras
            
            // Get the notification text/title
            val title = extras.getCharSequence("android.title")?.toString() ?: ""
            val text = extras.getCharSequence("android.text")?.toString() ?: ""
            val bigText = extras.getCharSequence("android.bigText")?.toString() ?: ""
            
            // DEBUG: Log ALL notifications to see what we're getting
            Log.i(TAG, "=== NOTIFICATION RECEIVED ===")
            Log.i(TAG, "Package: $packageName")
            Log.i(TAG, "Title: $title")
            Log.i(TAG, "Text: $text")
            Log.i(TAG, "BigText: $bigText")
            
            // Check if this is from PhonePe specifically (for debugging)
            if (packageName.contains("phonepe", ignoreCase = true)) {
                Log.w(TAG, "📱 PHONEPE NOTIFICATION DETECTED!")
                Log.w(TAG, "Package: $packageName")
                Log.w(TAG, "Title: $title")
                Log.w(TAG, "Text: $text")
                Log.w(TAG, "BigText: $bigText")
            }
            
            // Check if this is from a UPI app
            if (UPI_APPS.contains(packageName)) {
                Log.w(TAG, "🎯 UPI Notification detected!")
                Log.w(TAG, "Package: $packageName")
                Log.w(TAG, "Title: $title")
                Log.w(TAG, "Text: $text")
                Log.w(TAG, "BigText: $bigText")
                
                // Parse UPI notification details
                parseUpiNotification(packageName, title, text, bigText)
            } else {
                Log.d(TAG, "Non-UPI notification ignored")
            }
            Log.i(TAG, "=== END NOTIFICATION ===")
        }
    }
    
    /**
     * Called when a notification is removed.
     * We don't need to handle this for now, but it's good to know.
     */
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        Log.d(TAG, "Notification removed")
    }
    
    /**
     * Parse UPI notification details.
     * This is where we'll extract amount, merchant, and other details.
     * For now, we'll just log what we find.
     */
    private fun parseUpiNotification(packageName: String, title: String, text: String, bigText: String) {
        Log.d(TAG, "=== Parsing UPI Notification ===")
        Log.d(TAG, "App: ${getAppName(packageName)}")
        Log.d(TAG, "Title: $title")
        Log.d(TAG, "Text: $text")
        Log.d(TAG, "BigText: $bigText")
        
        // Combine all text to search for transaction details
        val fullText = "$title $text $bigText"
        
        // Parse amount (Rs.1.00, ₹250, etc.)
        val amountPattern = Regex("(?:Rs\\.?|₹)\\s*([\\d,]+\\.?\\d*)")
        val amountMatch = amountPattern.find(fullText)
        val amount = amountMatch?.groupValues?.get(1)?.replace(",", "") ?: "0.00"
        
        // Parse transaction type (Debit/Credit)
        val isDebit = fullText.contains("debited", ignoreCase = true) || 
                     fullText.contains("Dr.", ignoreCase = true) ||
                     fullText.contains("sent", ignoreCase = true)
        
        // Parse account number (A/C XXXXXX9234)
        val accountPattern = Regex("A/C\\s+([A-Z0-9]+)")
        val accountMatch = accountPattern.find(fullText)
        val accountNumber = accountMatch?.groupValues?.get(1) ?: ""
        
        // Parse reference number (Ref:979519041132)
        val refPattern = Regex("(?:Ref|Reference)\\s*:?\\s*([A-Z0-9]+)")
        val refMatch = refPattern.find(fullText)
        val referenceNumber = refMatch?.groupValues?.get(1) ?: ""
        
        // Parse available balance (AvlBal:Rs825.98)
        val balancePattern = Regex("(?:AvlBal|Balance)\\s*:?\\s*(?:Rs\\.?|₹)\\s*([\\d,]+\\.?\\d*)")
        val balanceMatch = balancePattern.find(fullText)
        val availableBalance = balanceMatch?.groupValues?.get(1)?.replace(",", "") ?: "0.00"
        
        // Parse recipient/VPA (8240835756-2@ybl)
        val vpaPattern = Regex("([\\d]+-[\\d]+@[a-z]+)")
        val vpaMatch = vpaPattern.find(fullText)
        val recipientVpa = vpaMatch?.groupValues?.get(1) ?: ""
        
        Log.i(TAG, "🔍 PARSED TRANSACTION DETAILS:")
        Log.i(TAG, "Amount: ₹$amount")
        Log.i(TAG, "Type: ${if (isDebit) "DEBIT" else "CREDIT"}")
        Log.i(TAG, "Account: $accountNumber")
        Log.i(TAG, "Reference: $referenceNumber")
        Log.i(TAG, "Available Balance: ₹$availableBalance")
        Log.i(TAG, "Recipient VPA: $recipientVpa")
        
        // TODO: Create Expense object and save to database
        // TODO: Update UI to show new transaction
        
        Log.d(TAG, "=== End Parsing ===")
    }
    
    /**
     * Get user-friendly app name from package name.
     */
    private fun getAppName(packageName: String): String {
        return when (packageName) {
            "com.google.android.apps.nbu.paisa.user" -> "Google Pay"
            "com.phonepe.app" -> "PhonePe"
            "net.one97.paytm" -> "Paytm"
            "com.amazon.mShop.android.shopping" -> "Amazon Pay"
            "in.org.npci.upiapp" -> "BHIM UPI"
            "com.csam.cbss.paytm" -> "Paytm Payments Bank"
            "com.yono" -> "YONO SBI"
            else -> "Unknown App"
        }
    }
    
    /**
     * Called when the listener is connected to the notification manager.
     */
    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.i(TAG, "🔔 Notification Listener Connected!")
        Log.i(TAG, "FlowSense AI is now listening for UPI notifications...")
    }
    
    /**
     * Called when the listener is disconnected from the notification manager.
     */
    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.w(TAG, "⚠️ Notification Listener Disconnected!")
    }
}
