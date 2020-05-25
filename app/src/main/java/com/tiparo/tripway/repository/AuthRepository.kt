package com.tiparo.tripway.repository

import androidx.lifecycle.LiveData
import com.tiparo.tripway.AppExecutors
import com.tiparo.tripway.models.AuthResponse
import com.tiparo.tripway.models.Resource
import com.tiparo.tripway.repository.network.api.ApiResponse
import com.tiparo.tripway.repository.network.api.services.AuthService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val appExecutors: AppExecutors
) {
    fun authUser(tokenId: String): LiveData<Resource<AuthResponse>> {
        return object : NetworkBoundResource<AuthResponse, AuthResponse>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<AuthResponse>> {
                return authService.authBackend(tokenId)
            }
        }.asLiveData()
    }
}