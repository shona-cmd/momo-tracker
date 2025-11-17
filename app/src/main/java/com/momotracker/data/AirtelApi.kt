package com.momotracker.data

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AirtelApi {
    @POST("auth/oauth2/token")
    suspend fun getAuthToken(@Body request: AirtelAuthRequest): AirtelAuthResponse

    @POST("merchant/v1/payments")
    suspend fun initiateCollection(
        @Header("Authorization") token: String,
        @Body request: AirtelCollectionRequest
    ): AirtelCollectionResponse
}
