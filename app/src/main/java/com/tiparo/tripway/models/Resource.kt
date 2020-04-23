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

package com.tiparo.tripway.models

import com.tiparo.tripway.repository.network.api.ErrorDescription

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: String? = null,
    val code: Int? = null,
    val params: Map<String, String?>? = null
) {
    enum class Status {
        SUCCESS, ERROR, LOADING
    }

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(
                Status.SUCCESS,
                data
            )
        }

        fun <T> error(data: T?, errorDescription: ErrorDescription): Resource<T> {
            return Resource(
                Status.ERROR,
                data,
                errorDescription.message,
                errorDescription.code,
                errorDescription.params
            )
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(
                Status.LOADING,
                data
            )
        }
    }
}