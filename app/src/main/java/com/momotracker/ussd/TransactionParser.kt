package com.momotracker.ussd

import com.momotracker.data.Transaction

object TransactionParser {

    fun parse(ussdText: String): Transaction? {
        // Parse USSD text to extract transaction details
        // This is a placeholder implementation
        // In a real implementation, use regex or string parsing to extract amount, type, etc.
        if (ussdText.contains("sent") || ussdText.contains("received")) {
            // Extract amount, date, etc.
            return Transaction(
                id = 0, // Auto-generated
                amount = 0.0, // Parse from text
                type = if (ussdText.contains("sent")) "sent" else "received",
                description = ussdText,
                date = System.currentTimeMillis()
            )
        }
        return null
    }
}
