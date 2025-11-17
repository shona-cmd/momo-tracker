package com.momotracker.data

interface AirtelPaymentRepository {
    suspend fun processPayment(phone: String): Boolean
}

class AirtelPaymentRepositoryImpl @javax.inject.Inject constructor(
    private val airtelApi: AirtelApi
) : AirtelPaymentRepository {
    override suspend fun processPayment(phone: String): Boolean {
        // Make API call to Airtel
        val request = AirtelPaymentRequest(phone = phone, amount = "5000")
        val response = airtelApi.processPayment(request)

        return response.isSuccessful // Or handle the response based on your API
    }
}
