package com.tiparo.tripway.models.repository.services.response

import com.tiparo.tripway.models.entities.Error

data class ResourceError(var error: String = "", val params: List<String> = listOf())

data class ResourceErrorDAO(var error: Error? = null, val params: List<String> = listOf())