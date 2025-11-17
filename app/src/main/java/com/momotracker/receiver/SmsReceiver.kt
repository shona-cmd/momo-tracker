package com.momotracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import android.util.Log
import com.momotracker.data.Transaction
import com.momotracker.viewmodel.TransactionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras
            if (bundle != null) {
                val pdus = bundle.get("pdus") as Array<*>?
                val format = bundle.getString("format")

                pdus?.forEach { pdu ->
                    val sms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        SmsMessage.createFromPdu(pdu as ByteArray, format)
                    } else {
                        SmsMessage.createFromPdu(pdu as ByteArray)
                    }

                    val messageBody = sms.messageBody
                    val sender = sms.originatingAddress ?: return@forEach

                    if (sender.contains("MTN") || sender.contains("AIRTEL") || sender == "MoMo" || sender == "173") {
                        parseAndSave(context, messageBody)
                    }
                }
            }
        }
    }

    private fun parseAndSave(context: Context, body: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val viewModel = TransactionViewModel(context.applicationContext as android.app.Application)
            val transaction = parseMtnMomo(body) ?: parseAirtelMoney(body) ?: return@launch
            viewModel.insert(transaction)
            Log.d("MomoTracker", "Saved: $transaction")
        }
    }

    private fun parseMtnMomo(text: String): Transaction? {
        // Example: "You have sent UGX 5,000 to 077xxxxxxx on 17/11/25 at 13:45. New balance: UGX 45,000. Txn ID: 251117XXXXXX"
        val sentPattern = Pattern.compile(
            "sent\\s+UGX ([\\d,]+).*?to\\s+([\\d]{10})|received\\s+UGX ([\\d,]+).*?from\\s+([\\d]{10})|fee of UGX ([\\d,]+).*?Txn ID[:\\s]+(\\w+)|balance[:\\s]+UGX ([\\d,]+)",
            Pattern.CASE_INSENSITIVE
        )
        val matcher = sentPattern.matcher(text.replace(".", ""))

        var amount = 0.0; var type = ""; var phone = ""; var name: String? = null; var txnId = ""; var balance: Double? = null

        while (matcher.find()) {
            when {
                matcher.group(1) != null -> { // Sent
                    amount = matcher.group(1).replace(",", "").toDouble()
                    phone = matcher.group(2) ?: ""
                    type = "Sent"
                }
                matcher.group(3) != null -> { // Received
                    amount = matcher.group(3).replace(",", "").toDouble()
                    phone = matcher.group(4) ?: ""
                    type = "Received"
                }
                matcher.group(5) != null -> { // Fee
                    amount = matcher.group(5).replace(",", "").toDouble()
                    type = "Fee"
                }
                matcher.group(6) != null -> txnId = matcher.group(6)
                matcher.group(7) != null -> balance = matcher.group(7).replace(",", "").toDouble()
            }
        }
        if (amount == 0.0) return null
        return Transaction(amount = amount, type = type, phone = phone, name = name, transactionId = txnId, balanceAfter = balance)
    }

    private fun parseAirtelMoney(text: String): Transaction? {
        // Example: "You have received UGX10,000 from 075xxxxxxx (John Doe). New balance UGX 123,456. Ref: AM251117XXXX"
        val pattern = Pattern.compile(
            "received\\s+UGX([\\d,]+).*?from\\s+([\\d]{10})(?:\\s+\\(([^)]+)\\))?.*?balance\\s+UGX ([\\d,]+)|sent\\s+UGX([\\d,]+).*?to\\s+([\\d]{10})",
            Pattern.CASE_INSENSITIVE
        )
        val m = pattern.matcher(text.replace(".", ""))

        if (!m.find()) return null

        return when {
            m.group(1) != null -> Transaction(
                amount = m.group(1).replace(",", "").toDouble(),
                type = "Received",
                phone = m.group(2) ?: "",
                name = m.group(3),
                transactionId = "",
                balanceAfter = m.group(4)?.replace(",", "")?.toDouble()
            )
            m.group(5) != null -> Transaction(
                amount = m.group(5).replace(",", "").toDouble(),
                type = "Sent",
                phone = m.group(6) ?: "",
                name = null,
                transactionId = "",
                balanceAfter = null
            )
            else -> null
        }
    }
}
