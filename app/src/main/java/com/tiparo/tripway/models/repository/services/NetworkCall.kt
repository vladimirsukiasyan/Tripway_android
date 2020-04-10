package com.tiparo.tripway.models.repository.services

import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.MutableLiveData
import com.tiparo.tripway.BaseApplication
import com.tiparo.tripway.models.repository.services.response.Resource
import com.tiparo.tripway.models.repository.services.response.ResourceErrorDAO
import com.tiparo.tripway.utils.ErrorUtils
import okhttp3.Interceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkCall<T> {
    private lateinit var call: Call<T>

    fun makeCall(call: Call<T>): MutableLiveData<Resource<T>> {
        this.call = call
        val callBackKt =
            CallBackKt<T>()

        callBackKt.result.value = Resource.loading(null)
        this.call.clone().enqueue(callBackKt)

        return callBackKt.result
    }

    class CallBackKt<T> : Callback<T> {
        var result: MutableLiveData<Resource<T>> = MutableLiveData()

        override fun onFailure(call: Call<T>, t: Throwable) {
            result.value = Resource.error(ResourceErrorDAO())
            t.printStackTrace()
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful) {
                result.value = Resource.success(response.body())
            } else {
                result.value =
                    Resource.error(ErrorUtils().parseError(response.errorBody(), response.code()))
            }
        }
    }

    class TokenInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            var request = chain.request()

            val preferences = BaseApplication.getInstance()
                .getSharedPreferences(BaseApplication.APP_NAME, MODE_PRIVATE)

            val sessionID = preferences.getString(HEADER_AUTHORIZATION, null)

            if (sessionID != null) {
                request = request.newBuilder()
                    .addHeader(HEADER_AUTHORIZATION, sessionID)
                    .build()
            }
            if (!request.url.encodedPath.contains("api/auth")) {
                throw IllegalAccessException()
            }

            val response = chain.proceed(request)
            val sessionId = response.header(HEADER_AUTHORIZATION)

            sessionId?.let{
                preferences.edit().putString(HEADER_AUTHORIZATION, it).commit()
            }
            return response
        }
    }
}