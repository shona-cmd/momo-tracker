package com.momotracker.data

data class YoPaymentRequest(
    val amount: String, // e.g., "5000"
    val phoneNumber: String, // e.g., "2567xxxxxxxx"
    val description: String = "Momo Tracker Pro Unlock"
)
