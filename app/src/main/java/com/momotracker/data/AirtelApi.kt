package com.momotracker.data

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AirtelApi {
    @POST("payment") // Replace with your actual endpoint
    suspend fun processPayment(@Body request: AirtelPaymentRequest): Response<ResponseBody>
}
