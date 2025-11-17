package com.momotracker.data

// AirtelPaymentRequest.kt
data class AirtelAuthRequest(
    val client_id: String,
    val client_secret: String,
    val grant_type: String = "client_credentials"
)

data class AirtelAuthResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)

data class AirtelCollectionRequest(
    val amount: String,  // "5000"
    val currency: String = "UGX",
    val country: String = "UG",
    val payer: Payer,
    val payer_callback_url: String? = null,
    val payment_description: String? = "Momo Tracker Pro Unlock"
)

data class Payer(
    val phone_number: String,  // e.g., "2567xxxxxxxx"
    val alias: String? = null
)

data class AirtelCollectionResponse(
    val transaction_id: String,
    val status: String,  // "SUCCESSFUL" or "PENDING"
    val amount: String
)
