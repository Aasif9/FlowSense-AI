package com.asif.flowsenseai.util

import com.asif.flowsenseai.data.notification.ParsedUpiTransaction

/**
 * Utility object for parsing UPI transaction notifications.
 * Uses multiple regex patterns with fallback to handle various Indian UPI formats.
 */
object UpiParser {
    
    /**
     * Parse UPI notification text and extract transaction details.
     * @param notificationText The notification text to parse
     * @return ParsedUpiTransaction if parsing succeeds, null otherwise
     */
    fun parseUpiNotification(notificationText: String): ParsedUpiTransaction? {
        val text = notificationText.trim()
        
        // Extract amount using multiple patterns
        val amount = extractAmount(text) ?: return null
        
        // Extract merchant name using multiple patterns
        val merchant = extractMerchant(text) ?: return null
        
        return ParsedUpiTransaction(
            amount = amount,
            merchant = merchant.trim()
        )
    }
    
    /**
     * Extract amount from notification text.
     * Supports various formats: ₹250, Rs.250, Rs 250, ₹250.50, etc.
     */
    private fun extractAmount(text: String): Double? {
        // Pattern 1: ₹ symbol with optional decimal (₹250, ₹250.50, ₹ 250)
        val rupeeSymbolPattern = Regex("₹\\s*([\\d,]+\\.?\\d*)")
        
        // Pattern 2: Rs. prefix with optional decimal (Rs.250, Rs.250.50)
        val rsWithDotPattern = Regex("Rs\\.\\s*([\\d,]+\\.?\\d*)")
        
        // Pattern 3: Rs prefix without dot (Rs 250, Rs250)
        val rsWithoutDotPattern = Regex("Rs\\s*([\\d,]+\\.?\\d*)")
        
        // Try each pattern in order
        rupeeSymbolPattern.find(text)?.let {
            return it.groupValues[1].replace(",", "").toDoubleOrNull()
        }
        
        rsWithDotPattern.find(text)?.let {
            return it.groupValues[1].replace(",", "").toDoubleOrNull()
        }
        
        rsWithoutDotPattern.find(text)?.let {
            return it.groupValues[1].replace(",", "").toDoubleOrNull()
        }
        
        return null
    }
    
    /**
     * Extract merchant name from notification text.
     * Looks for merchant after "to" or "paid to" keywords.
     */
    private fun extractMerchant(text: String): String? {
        // Pattern 1: "paid to [merchant]" (e.g., "₹250 paid to Zomato")
        val paidToPattern = Regex("paid\\s+to\\s+([A-Za-z0-9\\s&]+?)(?:\\s+via|\\s+on|\\s+using|$)", RegexOption.IGNORE_CASE)
        
        // Pattern 2: "to [merchant]" (e.g., "Paid ₹430 to Swiggy")
        val toPattern = Regex("\\bto\\s+([A-Za-z0-9\\s&]+?)(?:\\s+via|\\s+on|\\s+using|$)", RegexOption.IGNORE_CASE)
        
        // Pattern 3: "Sent [amount] to [merchant]" (e.g., "Sent ₹150 to Rahul")
        val sentToPattern = Regex("sent\\s+(?:₹|Rs\\.?\\s*)?[\\d,]+\\.?\\d*\\s+to\\s+([A-Za-z0-9\\s&]+?)(?:\\s+via|\\s+on|\\s+using|$)", RegexOption.IGNORE_CASE)
        
        // Pattern 4: "transaction of [amount] to [merchant]" (e.g., "UPI transaction of Rs 520 to Amazon")
        val transactionToPattern = Regex("transaction\\s+of\\s+(?:₹|Rs\\.?\\s*)?[\\d,]+\\.?\\d*\\s+to\\s+([A-Za-z0-9\\s&]+?)(?:\\s+via|\\s+on|\\s+using|$)", RegexOption.IGNORE_CASE)
        
        // Try each pattern in order
        paidToPattern.find(text)?.let {
            return it.groupValues[1].trim()
        }
        
        sentToPattern.find(text)?.let {
            return it.groupValues[1].trim()
        }
        
        transactionToPattern.find(text)?.let {
            return it.groupValues[1].trim()
        }
        
        toPattern.find(text)?.let {
            return it.groupValues[1].trim()
        }
        
        return null
    }
}
