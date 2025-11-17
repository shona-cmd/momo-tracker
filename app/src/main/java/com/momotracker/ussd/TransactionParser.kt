package com.momotracker.ussd

import com.momotracker.ai.CategoryClassifier
import com.momotracker.data.Transaction
import java.util.regex.Pattern

object TransactionParser {
    private val PATTERN = Pattern.compile(
        "(?:Sent|Received).*UGX\\s*([\\d,]+).*?(\\d{9}).*?Ref[:\\s]*(\\w+)",
        Pattern.CASE_INSENSITIVE or Pattern.DOTALL
    )

    fun parse(raw: String): Transaction? {
        val m = PATTERN.matcher(raw)
        if (!m.find()) return null

        val amountStr = m.group(1).replace(",", "")
        val amount = amountStr.toLongOrNull() ?: return null

        // AI: categorize based on full USSD text
        val category = CategoryClassifier.classify(raw)

        return Transaction(
            amount = amount,
            phone = m.group(2),
            ref = m.group(3),
            timestamp = System.currentTimeMillis(),
            type = if (raw.contains("Sent", ignoreCase = true)) "DEBIT" else "CREDIT",
            category = category
        )
    }
}
