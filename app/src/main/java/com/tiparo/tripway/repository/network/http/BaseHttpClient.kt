package com.tiparo.tripway.repository.network.http

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.tiparo.tripway.repository.network.api.HEADER_ACCEPT_LANGUAGE
import com.tiparo.tripway.repository.network.api.SET_TOKEN
import com.tiparo.tripway.utils.ApiInvocationException
import com.tiparo.tripway.utils.LocaleUtil
import com.tiparo.tripway.utils.NullOnEmptyConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
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
        .addInterceptor(GeneralResponseInterceptor(application))
        .build()


    private fun createRetrofit() =
        Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(NullOnEmptyConverterFactory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()


    /**
     * Set, add authorization token using FirebaseAuth
     */

    class TokenInterceptor(val application: Application) : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            //REQUEST

            var request = chain.request()
            val requestBuilder = request.newBuilder()

//            val preferences = application
//                .getSharedPreferences((application as BaseApplication).APP_NAME, Context.MODE_PRIVATE)
//            val sessionID = preferences.getString(SET_COOKIE_SESSION_ID, null)
//            Log.d(TAG, "[TOKEN_INTERCEPTOR] $sessionID")
//
//            sessionID?.let {
//                requestBuilder.addHeader(SET_COOKIE_SESSION_ID, it)
//            }

            FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.result?.token?.let { token ->
                requestBuilder.addHeader(SET_TOKEN, token)
            } ?: //todo some exception

            requestBuilder.addHeader(HEADER_ACCEPT_LANGUAGE, LocaleUtil.getLanguage())

            val response = chain.proceed(requestBuilder.build())

            // RESPONSE

//            val setCookie = response.header(HEADER_SET_COOKIE);
//            setCookie?.let {
//                val httpCookie = Cookie.parse(BuildConfig.BASE_URL.toHttpUrlOrNull()!!, it)
//                if (httpCookie?.name == SET_COOKIE_SESSION_ID) {
//                    Log.d(TAG, "[SET_COOKIE_SESSION_ID] cookie has been set")
//                    preferences.edit().putString(SET_COOKIE_SESSION_ID, httpCookie.value).apply()
//                }
//            }
            return response
        }
    }

    class GeneralResponseInterceptor(val application: Application) : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val requestBuilder = request.newBuilder()
            val response = chain.proceed(requestBuilder.build())

            if (!response.isSuccessful || response.code == 204){
                throw ApiInvocationException(response.code, response.body?.string())
            }
            return response
        }
    }
}