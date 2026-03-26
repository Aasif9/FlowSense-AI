package com.asif.flowsenseai.util

import android.util.Log
import com.asif.flowsenseai.data.notification.ParsedUpiTransaction

/**
 * Single source of truth for parsing UPI and bank SMS notifications.
 * Handles: Google Pay, PhonePe, Paytm, Paytm Bank SMS, HDFC, SBI, BOB, Axis, ICICI
 */
object UpiParser {

    private const val TAG = "UpiParser"

    /**
     * Main entry point. Pass the full notification text (title + body combined).
     * Returns null if this doesn't look like a payment notification.
     */
    fun parse(title: String, body: String): ParsedUpiTransaction? {
        val combined = "$title $body".trim()
        Log.d(TAG, "Attempting to parse: $combined")

        // Must contain a money amount — if not, skip immediately
        val amount = extractAmount(combined) ?: run {
            Log.d(TAG, "No amount found, skipping.")
            return null
        }

        // Must look like a debit/payment — we don't care about credit/OTP/offer SMSes
        if (!isPaymentNotification(combined)) {
            Log.d(TAG, "Not a payment notification, skipping.")
            return null
        }

        val merchant = extractMerchant(combined) ?: "Unknown"
        val type = if (isDebit(combined)) "debit" else "credit"

        Log.i(TAG, "✅ Parsed — Amount: ₹$amount | Merchant: $merchant | Type: $type")

        return ParsedUpiTransaction(
            amount = amount,
            merchant = merchant,
            type = type,
            timestamp = System.currentTimeMillis()
        )
    }

    // ─── Amount extraction ────────────────────────────────────────────────────

    private fun extractAmount(text: String): Double? {
        val patterns = listOf(
            // ₹250, ₹ 250, ₹250.50
            Regex("₹\\s*([\\d,]+\\.?\\d*)"),
            // Rs.250, Rs. 250, Rs.250.50
            Regex("Rs\\.\\s*([\\d,]+\\.?\\d*)"),
            // Rs 250 (no dot)
            Regex("(?i)Rs\\s+([\\d,]+\\.?\\d*)"),
            // INR 250
            Regex("(?i)INR\\s*([\\d,]+\\.?\\d*)")
        )

        for (pattern in patterns) {
            val match = pattern.find(text) ?: continue
            val raw = match.groupValues[1].replace(",", "")
            val amount = raw.toDoubleOrNull() ?: continue
            if (amount > 0) return amount
        }
        return null
    }

    // ─── Is this actually a payment/debit notification? ──────────────────────

    private fun isPaymentNotification(text: String): Boolean {
        val paymentKeywords = listOf(
            "paid", "payment", "sent", "debited", "dr.", " dr ", "debit",
            "transferred", "transaction", "upi", "a/c", "acct", "spent"
        )
        val lower = text.lowercase()
        return paymentKeywords.any { lower.contains(it) }
    }

    private fun isDebit(text: String): Boolean {
        val lower = text.lowercase()
        val debitKeywords = listOf("debited", "dr.", " dr ", "debit", "paid", "sent", "spent", "payment")
        val creditKeywords = listOf("credited", "cr.", " cr ", "credit", "received", "refund")

        val debitScore = debitKeywords.count { lower.contains(it) }
        val creditScore = creditKeywords.count { lower.contains(it) }

        return debitScore >= creditScore // default to debit when ambiguous
    }

    // ─── Merchant extraction ─────────────────────────────────────────────────

    private fun extractMerchant(text: String): String? {
        // Strategy 1: "paid to X", "sent to X", "payment to X"
        val toPatterns = listOf(
            Regex("(?i)(?:paid|sent|payment|transferred)\\s+to\\s+([\\w\\s@.&]+?)(?:\\s+(?:via|on|using|ref|a/c|upi)|[,.]|$)"),
            Regex("(?i)to\\s+([\\w\\s@.&]+?)(?:\\s+(?:via|on|using|ref|a/c|upi)|[,.]|$)")
        )

        for (pattern in toPatterns) {
            val match = pattern.find(text) ?: continue
            val candidate = match.groupValues[1].trim()
            if (candidate.isNotBlank() && candidate.length > 1) {
                return cleanMerchantName(candidate)
            }
        }

        // Strategy 2: UPI VPA (e.g., zomato@okaxis, 9876543210@ybl)
        val vpaPattern = Regex("([\\w.\\-]+@[a-zA-Z]+)")
        val vpaMatch = vpaPattern.find(text)
        if (vpaMatch != null) {
            val vpa = vpaMatch.groupValues[1]
            // Try to extract readable name from VPA prefix
            val prefix = vpa.substringBefore("@")
            // If it's a phone number VPA, return the VPA itself
            return if (prefix.all { it.isDigit() || it == '-' }) vpa else prefix.capitalize()
        }

        // Strategy 3: Bank SMS — "Cr. to 8240835756-2@ybl" or "to A/C"
        val acPattern = Regex("(?i)to\\s+(?:a/c|acct|account)\\s*[:\\-]?\\s*([xX0-9]{4,})")
        val acMatch = acPattern.find(text)
        if (acMatch != null) {
            return "A/C ${acMatch.groupValues[1]}"
        }

        return null
    }

    private fun cleanMerchantName(raw: String): String {
        // Remove trailing noise words
        val noiseWords = listOf("via", "on", "using", "ref", "upi", "a/c")
        var cleaned = raw.trim()
        for (noise in noiseWords) {
            if (cleaned.lowercase().endsWith(noise)) {
                cleaned = cleaned.dropLast(noise.length).trim()
            }
        }
        return cleaned.trim().ifBlank { raw.trim() }
    }

    private fun String.capitalize(): String {
        return this.lowercase().replaceFirstChar { it.uppercase() }
    }
}
