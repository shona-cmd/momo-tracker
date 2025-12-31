package com.momotracker.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface YoApi {
    @POST("payments") // Replace with actual Yo Uganda API endpoint
    suspend fun processPayment(@Body request: YoPaymentRequest): Response<YoPaymentResponse>
}
