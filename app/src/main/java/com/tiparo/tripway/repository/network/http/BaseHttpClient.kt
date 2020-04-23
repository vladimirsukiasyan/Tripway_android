package com.tiparo.tripway.repository.network.http

import android.app.Application
import android.content.Context
import android.util.Log
import com.android.example.github.util.LiveDataCallAdapterFactory
import com.tiparo.tripway.BaseApplication
import com.tiparo.tripway.BuildConfig
import com.tiparo.tripway.repository.network.api.HEADER_ACCEPT_LANGUAGE
import com.tiparo.tripway.repository.network.api.HEADER_SET_COOKIE
import com.tiparo.tripway.repository.network.api.SET_COOKIE_SESSION_ID
import com.tiparo.tripway.utils.LocaleUtil
import com.tiparo.tripway.utils.NullOnEmptyConverterFactory
import com.tiparo.tripway.views.ui.TAG
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class BaseHttpClient constructor(
    private val baseURL: String,
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
            .baseUrl(baseURL)
            .addConverterFactory(NullOnEmptyConverterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(okHttpClient)
            .build()


    /**
     * Set, add authorization cookie using SharedPreferences, so it needs receive application
     */

    class TokenInterceptor(val application: Application) : Interceptor {

        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            //REQUEST

            var request = chain.request()
            val requestBuilder = request.newBuilder()

            val preferences = application
                .getSharedPreferences((application as BaseApplication).APP_NAME, Context.MODE_PRIVATE)
            val sessionID = preferences.getString(SET_COOKIE_SESSION_ID, null)
            Log.d(TAG, "[TOKEN_INTERCEPTOR] $sessionID")

            sessionID?.let {
                requestBuilder.addHeader(SET_COOKIE_SESSION_ID, it)
            }

            requestBuilder.addHeader(HEADER_ACCEPT_LANGUAGE, LocaleUtil.getLanguage())

            val response = chain.proceed(requestBuilder.build())

            // RESPONSE

            val setCookie = response.header(HEADER_SET_COOKIE);
            setCookie?.let {
                val httpCookie = Cookie.parse(BuildConfig.BASE_URL.toHttpUrlOrNull()!!, it)
                if (httpCookie?.name == SET_COOKIE_SESSION_ID) {
                    Log.d(TAG, "[SET_COOKIE_SESSION_ID] cookie has been set")
                    preferences.edit().putString(SET_COOKIE_SESSION_ID, httpCookie.value).apply()
                }
            }
            return response
        }
    }
}