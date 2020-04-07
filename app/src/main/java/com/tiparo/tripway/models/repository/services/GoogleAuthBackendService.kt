package com.tiparo.tripway.models.repository.services

import com.tiparo.tripway.models.entities.response.AuthResponse
import com.tiparo.tripway.utils.InjectorUtils
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface GoogleAuthBackendService {
    @GET("api/dummy")
    fun authBackend(@Header("Token-ID") idToken: String): Call<AuthResponse>

    companion object Factory {
        fun create(): GoogleAuthBackendService {
            return InjectorUtils.retrofit.create(GoogleAuthBackendService::class.java)
        }
    }
}