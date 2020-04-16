package com.tiparo.tripway.repository.network.api.services

import androidx.lifecycle.LiveData
import com.tiparo.tripway.models.AuthResponse
import com.tiparo.tripway.repository.network.api.ApiResponse
import retrofit2.http.Header
import retrofit2.http.POST

interface GoogleAuthBackendService {

    @POST("tripway/auth")
    fun authBackend(@Header("Token-ID") idToken: String): LiveData<ApiResponse<AuthResponse>>

}