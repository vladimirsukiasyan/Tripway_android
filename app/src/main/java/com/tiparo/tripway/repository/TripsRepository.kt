package com.tiparo.tripway.repository

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.tiparo.tripway.AppExecutors
import com.tiparo.tripway.BuildConfig
import com.tiparo.tripway.models.Point
import com.tiparo.tripway.models.Resource
import com.tiparo.tripway.models.Trip
import com.tiparo.tripway.repository.database.PointDao
import com.tiparo.tripway.repository.database.TripDao
import com.tiparo.tripway.repository.network.api.ApiResponse
import com.tiparo.tripway.repository.network.api.ApiSuccessResponse
import com.tiparo.tripway.repository.network.api.services.GoogleMapsServices
import com.tiparo.tripway.repository.network.api.services.ReverseGeocodingResponse
import com.tiparo.tripway.repository.network.api.services.ReverseGeocodingResponse.GeocodingResult
import com.tiparo.tripway.repository.network.api.services.TripsService
import com.tiparo.tripway.utils.FileUtils
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.FileOutputStream
import javax.inject.Inject

class TripsRepository @Inject constructor(
    private val application: Application,
    private val appExecutors: AppExecutors,
    private val tripDao: TripDao,
    private val pointDao: PointDao,
    private val tripsService: TripsService,
    private val googleMapsService: GoogleMapsServices
) {

    private val tripsMock = MutableList(10) { id ->
        TripsService.Trip(
            id = id.toString(),
            trip_name = "Trip $id",
            is_completed = id % 2 == 0,
            first_point_name = "Tokyo",
            last_point_name = "Baikal",
            user_id = "id$id"
        )
    }

    suspend fun loadTrips(): Resource<List<Trip>> = withContext(Dispatchers.IO){
        val trips = tripDao.getTrips()
        return@withContext Resource.success(trips)
    }

    fun loadTripsMock(): Resource<List<TripsService.Trip>> {
        return Resource.success(tripsMock)
    }
}