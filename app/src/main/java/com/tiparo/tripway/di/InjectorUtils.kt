package com.tiparo.tripway.di

import android.app.Application
import com.tiparo.tripway.models.repository.AuthRepository
import com.tiparo.tripway.models.repository.services.NetworkCall
import com.tiparo.tripway.models.repository.services.NullOnEmptyConverterFactory
import com.tiparo.tripway.utils.HTTP_AUTH_BACKEND
import com.tiparo.tripway.viewmodels.SignInViewModelFactory
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
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

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(NetworkCall.TokenInterceptor())
        .build()

    val retrofit: Retrofit = createRetrofit(HTTP_AUTH_BACKEND.toHttpUrlOrNull()!!)

    fun createRetrofit(url: HttpUrl): Retrofit = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(NullOnEmptyConverterFactory()) // TODO просто интересно, а какую задачу решаете?
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
}
