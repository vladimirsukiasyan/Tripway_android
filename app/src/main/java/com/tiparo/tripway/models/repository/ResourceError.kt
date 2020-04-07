package com.tiparo.tripway.models.repository

import com.tiparo.tripway.models.entities.Error

data class ResourceError(var error: String = "", val params: List<String> = listOf())

data class ResourceErrorDAO(var error: Error? = null, val params: List<String> = listOf())