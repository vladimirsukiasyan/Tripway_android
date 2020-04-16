package com.tiparo.tripway.repository.network.http

import android.app.Application
import android.content.Context
import com.android.example.github.util.LiveDataCallAdapterFactory
import com.tiparo.tripway.BaseApplication
import com.tiparo.tripway.BuildConfig
import com.tiparo.tripway.repository.network.api.HEADER_AUTHORIZATION
import com.tiparo.tripway.repository.network.environment.Environment
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BaseHttpClient constructor(
    private val environment: Environment,
    private val application: Application
) : HttpClient {

    private val okHttpClient: OkHttpClient
    private val retrofit: Retrofit

    init {
        okHttpClient = createOkHttpClient()
        retrofit = createRetrofit()
    }

    override fun <T> getApiService(apiServiceClass: Class<T>): T {
        return retrofit.create(apiServiceClass)
    }

    override fun getRetrofit(): Retrofit {
        return retrofit
    }

    private fun createOkHttpClient() = OkHttpClient.Builder()
        .addInterceptor(TokenInterceptor(application))
        .build()


    private fun createRetrofit() =
        Retrofit.Builder()
            .baseUrl(environment.getAPITripwayBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(okHttpClient)
            .build()

    class TokenInterceptor(val application: Application) : Interceptor {

        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            var request = chain.request()

            val preferences = application
                .getSharedPreferences((application as BaseApplication).APP_NAME, Context.MODE_PRIVATE)

            val sessionID = preferences.getString(HEADER_AUTHORIZATION, null)

            if (sessionID != null) {
                request = request.newBuilder()
                    .addHeader(HEADER_AUTHORIZATION, sessionID)
                    .build()
            }
            if (!request.url.encodedPath.contains("/auth")) {
                throw IllegalAccessException()
            }

            val response = chain.proceed(request)

            val setCookie = response.header("Set-Cookie");
            setCookie?.let {
                val httpCookie = Cookie.parse(BuildConfig.BASE_URL.toHttpUrlOrNull()!!, it)
                if (httpCookie?.name == HEADER_AUTHORIZATION) {
                    preferences.edit().putString(HEADER_AUTHORIZATION, httpCookie.value).apply()
                }
            }
            return response
        }
    }
}