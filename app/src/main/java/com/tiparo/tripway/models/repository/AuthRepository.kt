package com.tiparo.tripway.models.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tiparo.tripway.models.repository.services.response.AuthResponse
import com.tiparo.tripway.models.repository.services.GoogleAuthBackendService
import com.tiparo.tripway.models.repository.services.NetworkCall
import com.tiparo.tripway.models.repository.services.response.Resource

class AuthRepository {
    private val service = GoogleAuthBackendService.create()

    private val authServerCall =
        NetworkCall<AuthResponse>()
    private var authResult = MutableLiveData<Resource<AuthResponse>>()

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