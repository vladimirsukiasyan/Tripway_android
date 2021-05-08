/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tiparo.tripway.repository

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.tiparo.tripway.AppExecutors
import com.tiparo.tripway.utils.Resource
import com.tiparo.tripway.repository.network.api.ApiEmptyResponse
import com.tiparo.tripway.repository.network.api.ApiErrorResponse
import com.tiparo.tripway.repository.network.api.ApiResponse
import com.tiparo.tripway.repository.network.api.ApiSuccessResponse

/**
 *
 *
 * You can read more about it in the [Architecture
 * Guide](https://developer.android.com/arch).
 * @param <ResultType>
 * @param <RequestType>
</RequestType></ResultType> */
@Suppress("UNCHECKED_CAST")
abstract class NetworkBoundResource<ResultType, RequestType>
@MainThread constructor(private val appExecutors: AppExecutors) {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        @Suppress("LeakingThis")
        fetchFromNetwork()
    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    private fun fetchFromNetwork() {
        val apiResponse = createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            when (response) {
                is ApiSuccessResponse -> {
                    val responseDTO = mapDTO(response)
                    setValue(Resource.success(responseDTO.body))
                }
                is ApiEmptyResponse -> {
                    setValue(Resource.success(null))
                }
                is ApiErrorResponse -> {
                    onFetchFailed()
                    setValue(Resource.error(null, response.errorDescription))
                }
            }
        }
    }

    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<RequestType>) = response.body

    @WorkerThread
    protected open fun mapDTO(response: ApiSuccessResponse<RequestType>): ApiSuccessResponse<ResultType> =
        response as ApiSuccessResponse<ResultType>

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>
}
