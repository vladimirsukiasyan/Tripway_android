package com.tiparo.tripway.models.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tiparo.tripway.models.entities.response.AuthResponse
import com.tiparo.tripway.models.repository.services.GoogleAuthBackendService

class AuthRepository {
    private val service = GoogleAuthBackendService.create()

    private val authServerCall = NetworkCall<AuthResponse>()
    private lateinit var authResult: MutableLiveData<Resource<AuthResponse>>

    fun authUser(tokenId: String): LiveData<Resource<AuthResponse>> {
        if (authResult.value?.status == Resource.Status.LOADING) {
            return authResult
        }
        authResult = authServerCall.makeCall(service.authBackend(tokenId))
        return authResult
    }

    companion object {
        // For Singleton instantiation
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: AuthRepository().also { instance = it }
            }
    }
}