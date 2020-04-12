package com.tiparo.tripway.models.repository.services.response

import com.tiparo.tripway.models.entities.Error

data class ResourceError(var error: String = "", val params: List<String> = listOf())

// TODO чаще всего по код стайлу аббревиатуры не пишут капсом. т.е. тут было бы ResourceErrorDao
data class ResourceErrorDAO(var error: Error? = null, val params: List<String> = listOf())

// TODO не понял зачем нужно три сущности: ResourceError, ResourcesErrorDao и Error. Сможешь пояснить?