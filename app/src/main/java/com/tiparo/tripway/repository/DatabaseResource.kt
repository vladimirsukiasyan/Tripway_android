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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.tiparo.tripway.AppExecutors
import com.tiparo.tripway.models.Resource
import com.tiparo.tripway.repository.network.api.ErrorDescription

abstract class DatabaseResource<ResultType>
@MainThread constructor(private val appExecutors: AppExecutors) {

    private val result = MediatorLiveData<Resource<ResultType>>()

    init {
        result.value = Resource.loading(null)
        try {
            @Suppress("LeakingThis")
            val dbSource = loadFromDb()
            result.addSource(dbSource) { data ->
                setValue(Resource.success(data))
            }
        } catch (exception: Exception) {
            setValue(
                Resource.error(
                    null,
                    ErrorDescription(exception.message ?: "Error when trying to load from database")
                )
            )
        }

    }

    @MainThread
    private fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.value = newValue
        }
    }

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>
}
