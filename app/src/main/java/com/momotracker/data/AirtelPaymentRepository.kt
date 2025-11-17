package com.momotracker.data

import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AirtelPaymentRepository @Inject constructor(
    private val api: AirtelApi  // Injected via Retrofit/Hilt
) {
    private val clientId = "YOUR_CLIENT_ID"  // From Airtel dashboard
    private val clientSecret = "YOUR_CLIENT_SECRET"

    suspend fun initiatePayment(
        phoneNumber: String,  // e.g., "2567xxxxxxxx"
        amount: String = "5000"
    ): Result<AirtelCollectionResponse> {
        return try {
            // Step 1: Get OAuth Token
            val authResponse = api.getAuthToken(
                AirtelAuthRequest(clientId, clientSecret)
            )
            val token = "${authResponse.token_type} ${authResponse.access_token}"

            // Step 2: Initiate Collection
            val collectionResponse = api.initiateCollection(
                token,
                AirtelCollectionRequest(
                    amount = amount,
                    payer = Payer(phoneNumber)
                )
            )

            // Step 3: Poll/Wait for status (Airtel sends async callback, but poll for smooth UX)
            delay(3000)  // Allow PIN processing
            // In prod: Listen for webhook POST with { "transaction": { "id": "abc123", "status": "SUCCESSFUL" } }

            if (collectionResponse.status == "SUCCESSFUL") {
                // Log to Firebase: "Airtel Payment Success"
                Result.success(collectionResponse)
            } else {
                Result.failure(Exception("Payment pending/failed: ${collectionResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
