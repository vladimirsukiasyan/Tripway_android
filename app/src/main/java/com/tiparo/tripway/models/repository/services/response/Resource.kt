package com.tiparo.tripway.models.repository.services.response

import okhttp3.Interceptor

class Resource<T> private constructor(
    val status: Status,
    val data: T?,
    val resourceError: ResourceErrorDAO?
) {
    enum class Status {
        SUCCESS, ERROR, LOADING
    }

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(
                Status.SUCCESS,
                data,
                null
            )
        }

        fun <T> error(resourceError: ResourceErrorDAO?): Resource<T> {
            return Resource(
                Status.ERROR,
                null,
                resourceError
            )
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(
                Status.LOADING,
                data,
                null
            )
        }
    }
}