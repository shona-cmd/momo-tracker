package com.momotracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: String, // "Sent", "Received", "Fee", "Cashout", "Airtime", etc.
    val phone: String,
    val name: String?,
    val transactionId: String,
    val balanceAfter: Double?,
    val date: Long = System.currentTimeMillis()
) {
    fun formattedDate(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        return sdf.format(Date(date))
    }

    fun formattedAmount(): String = "UGX ${String.format("%,.0f", amount)}"
}
