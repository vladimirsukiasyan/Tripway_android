package com.tiparo.tripway.repository.network.environment

import com.tiparo.tripway.BuildConfig

class BaseEnvironment : Environment {

    private var baseUrl = BuildConfig.BASE_URL

    override fun getAPITripwayBaseUrl(): String {
        return baseUrl
    }
}