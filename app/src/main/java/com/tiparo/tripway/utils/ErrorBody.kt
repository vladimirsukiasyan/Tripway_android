package com.tiparo.tripway.utils

data class ErrorBody(var message: String? = null, var code: Int? = null, val type: ErrorType) {
    enum class ErrorType {
        GENERAL,
        NO_CONTENT,
        NO_INTERNET,
        FAILED_COMPRESSION
    }

    companion object {
        fun fromException(error: Throwable?): ErrorBody =
            when (error) {
                is java.io.IOException -> {
                    ErrorBody(error.message, type = ErrorType.NO_INTERNET)
                }
                is ApiInvocationException -> {
                    fromServerException(error)
                }
                is ClientException -> {
                    fromClientException(error)
                }
                else -> ErrorBody(error?.message, type = ErrorType.GENERAL)
            }

        private fun fromServerException(exception: ApiInvocationException): ErrorBody =
            ErrorBody(exception.errorMessage, exception.errorCode, when (exception.errorCode) {
                204 -> ErrorType.NO_CONTENT
                else -> ErrorType.GENERAL
            })

        private fun fromClientException(exception: ClientException): ErrorBody =
            ErrorBody(exception.errorMessage, type = when (exception.errorCodeType) {
                ClientException.Code.ImageCompressionException -> ErrorType.FAILED_COMPRESSION
                else -> ErrorType.GENERAL
            })
    }
}