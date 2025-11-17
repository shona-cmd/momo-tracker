package com.momotracker.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Headers
import java.util.UUID

interface MtnMomoApi {
    @POST("collection/token/")
    suspend fun getAuthToken(
        @Header("X-Target-Environment") environment: String,
        @Header("Authorization") authorization: String,
        @Body request: MtnAuthRequest
    ): MtnAuthResponse

    @POST("collection/v1_0/requesttopay")
    suspend fun initiateCollection(
        @Header("Authorization") authorization: String,
        @Header("X-Reference-Id") referenceId: String,
        @Header("X-Callback-Url") callbackUrl: String,
        @Header("X-Target-Environment") environment: String,
        @Header("Ocp-Apim-Subscription-Key") subscriptionKey: String,
        @Body request: MtnCollectionRequest
    ): MtnCollectionResponse
}
