package com.tiparo.tripway.utils

import android.app.Application
import com.tiparo.tripway.models.repository.AuthRepository
import com.tiparo.tripway.models.repository.services.GoogleAuthBackendService
import com.tiparo.tripway.viewmodels.SignInViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    private fun getAuthRepository() = AuthRepository.getInstance()

    fun provideSignInViewModelFactory(application: Application): SignInViewModelFactory {
        val repository = getAuthRepository()
        return SignInViewModelFactory(repository, application)
    }

    val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(HTTP_AUTH_BACKEND)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

}
