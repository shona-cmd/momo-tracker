package com.momotracker.data

import javax.inject.Inject
import kotlin.Result

class YoPaymentRepository @Inject constructor(
    private val api: YoApi  // Injected via Retrofit/Hilt
) {
    suspend fun initiatePayment(
        phoneNumber: String,
        amount: String
    ): Result<YoPaymentResponse> {
        return try {
            val response = api.processPayment(
                YoPaymentRequest(
                    amount = amount,
                    phoneNumber = phoneNumber
                )
            )
            if (response.isSuccessful) {
                // Assuming Yo API returns a response body with transaction details
                Result.success(YoPaymentResponse(transactionId = "YO_${System.currentTimeMillis()}"))
            } else {
                Result.failure(Exception("Yo payment failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

data class YoPaymentResponse(
    val transactionId: String
)
