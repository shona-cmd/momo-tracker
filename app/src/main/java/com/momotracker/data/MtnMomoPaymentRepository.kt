package com.momotracker.data

import kotlinx.coroutines.delay
import java.util.Base64
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MtnMomoPaymentRepository @Inject constructor(
    private val api: MtnMomoApi  // Injected via Retrofit/Hilt
) {
    private val baseUrl = "https://sandbox.momodeveloper.mtn.com/"  // Switch to prod
    private val userId = "YOUR_USER_ID"  // From dashboard
    private val apiKey = "YOUR_API_KEY"
    private val subscriptionKey = "YOUR_PRIMARY_SUBSCRIPTION_KEY"
    private val basicAuth = "Basic ${Base64.getEncoder().encodeToString("$userId:$apiKey".toByteArray())}"

    suspend fun initiatePayment(
        phoneNumber: String,  // e.g., "256712345678"
        amount: String = "5000",
        externalId: String = UUID.randomUUID().toString()
    ): Result<MtnCollectionResponse> {
        return try {
            // Step 1: Get OAuth Token
            val authResponse = api.getAuthToken(userId, basicAuth, MtnAuthRequest())
            val token = "${authResponse.token_type} ${authResponse.access_token}"

            // Step 2: Initiate Request-to-Pay
            val referenceId = UUID.randomUUID().toString()
            val collectionResponse = api.initiateCollection(
                token,
                referenceId,
                "https://yourapp.com/mtn-callback",  // Your webhook
                "sandbox",  // or "production"
                subscriptionKey,
                MtnCollectionRequest(
                    amount = amount,
                    externalId = externalId,
                    payer = MtnPayer(phoneNumber)
                )
            )

            // Step 3: Poll/Wait for status (MTN sends async webhook, but poll for smooth UX)
            delay(3000)  // Allow PIN processing
            // In prod: Listen for webhook POST with { "status": "SUCCESSFUL", "financialTransactionId": "abc123" }

            if (collectionResponse.status == "SUCCESSFUL") {
                // Log to Firebase: "MTN Payment Success"
                Result.success(collectionResponse)
            } else {
                Result.failure(Exception("Payment pending/failed: ${collectionResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
