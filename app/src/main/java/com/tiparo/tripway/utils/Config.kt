package com.tiparo.tripway.utils

import com.tiparo.tripway.BuildConfig

val HTTP_AUTH_BACKEND = if (BuildConfig.port != 80) {
    "${BuildConfig.scheme}://${BuildConfig.host}:${BuildConfig.port}/"
} else {
    "${BuildConfig.scheme}://${BuildConfig.host}/"
}