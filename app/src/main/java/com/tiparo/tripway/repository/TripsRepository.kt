package com.tiparo.tripway.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.tiparo.tripway.AppExecutors
import com.tiparo.tripway.models.Resource
import com.tiparo.tripway.models.Trip
import com.tiparo.tripway.models.TripWithPoints
import com.tiparo.tripway.repository.database.PointDao
import com.tiparo.tripway.repository.database.TripDao
import com.tiparo.tripway.repository.network.api.ErrorDescription
import com.tiparo.tripway.repository.network.api.services.TripsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class TripsRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    private val tripDao: TripDao,
    private val pointDao: PointDao
) {

    fun loadTrips(): LiveData<Resource<List<Trip>>> {
        return object : DatabaseResource<List<Trip>>(appExecutors) {
            override fun loadFromDb(): LiveData<List<Trip>> {
                return tripDao.getTrips()
            }

        }.asLiveData()
    }

    suspend fun loadTripWithPoints(tripId: Long): Resource<TripWithPoints> =
        withContext(Dispatchers.IO) {
            try {
                val tripWithPoints = tripDao.getTripWithPoints(tripId)
                Timber.d(tripWithPoints.toString())
                Resource.success(tripWithPoints)
            } catch (exception: Exception){
                //TODO сделать нормальное логирование и extract string
                Timber.e(exception)
                Resource.error(null, ErrorDescription("Error when trying to load from database"))
            }
        }
}