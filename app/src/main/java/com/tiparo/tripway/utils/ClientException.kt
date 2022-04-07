package com.tiparo.tripway.utils

class ClientException(
    val errorCodeType: Code,
    val errorMessage: String? = null
) : Exception(errorMessage) {
    enum class Code {
        ImageCompressionException,
    }
}