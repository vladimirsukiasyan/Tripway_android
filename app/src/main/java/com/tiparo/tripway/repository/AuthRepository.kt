package com.tiparo.tripway.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tiparo.tripway.AppExecutors
import com.tiparo.tripway.models.AuthResponse
import com.tiparo.tripway.models.Resource
import com.tiparo.tripway.repository.network.api.ApiResponse
import com.tiparo.tripway.repository.network.api.services.GoogleAuthBackendService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authService: GoogleAuthBackendService,
    private val appExecutors: AppExecutors
) {
    fun authUser(tokenId: String): LiveData<Resource<AuthResponse>> {
        return object : NetworkBoundResource<AuthResponse, AuthResponse>(appExecutors) {
            override fun saveCallResult(item: AuthResponse) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun shouldFetch(data: AuthResponse?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<AuthResponse> {
                return MutableLiveData()
            }

            override fun createCall(): LiveData<ApiResponse<AuthResponse>> {
                return authService.authBackend(tokenId)
            }
        }.asLiveData()
    }
}