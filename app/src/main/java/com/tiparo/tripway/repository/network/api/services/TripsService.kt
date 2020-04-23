package com.tiparo.tripway.repository.network.api.services

import androidx.lifecycle.LiveData
import com.tiparo.tripway.repository.network.api.ApiResponse
import retrofit2.http.GET

interface TripsService {

    @GET("tripway/trips/own")
    fun getOwnTrips(): LiveData<ApiResponse<List<Trip>>>

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