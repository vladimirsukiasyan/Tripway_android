package com.tiparo.tripway.repository.network.api.services

import com.tiparo.tripway.discovery.api.dto.DiscoveryInfo
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Query

interface TripsService {

    @GET("discovery")
    fun getTripsDiscoveryPage(@Query("anchor") anchor: String?): Single<DiscoveryInfo>

    //TODO Решить вопрос с размещение модели в пакетах
    data class Trip(
        val id: String,
        val trip_name: String,
        val is_completed: Boolean,
        val first_point_name: String,
        val last_point_name: String,
        val user_id: String?
    )
}