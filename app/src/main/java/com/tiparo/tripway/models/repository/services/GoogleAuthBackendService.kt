package com.tiparo.tripway.models.repository.services

import com.tiparo.tripway.di.InjectorUtils
import com.tiparo.tripway.models.repository.services.response.AuthResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface GoogleAuthBackendService {

    @POST("tripway/auth")
    fun authBackend(@Header("Token-ID") idToken: String): Call<AuthResponse>


    companion object Factory {
        fun create(): GoogleAuthBackendService {
            return InjectorUtils.retrofit.create(
                GoogleAuthBackendService::class.java
            )
        }
    }
}