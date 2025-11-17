package com.momotracker.data

data class MtnAuthRequest(
    val grant_type: String = "client_credentials"
)

data class MtnAuthResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)

data class MtnCollectionRequest(
    val amount: String,  // "5000"
    val currency: String = "UGX",
    val externalId: String,  // Your unique order ID, e.g., UUID
    val payer: MtnPayer,
    val payerMessage: String = "Momo Tracker Pro Unlock",
    val payeeNote: String = "One-time app purchase"
)

data class MtnPayer(
    val partyIdType: String = "MSISDN",
    val partyId: String  // e.g., "2567xxxxxxxx"
)

data class MtnCollectionResponse(
    val financialTransactionId: String,
    val status: String,  // "PENDING", "SUCCESSFUL", "FAILED"
    val amount: String
)
