package com.tiparo.tripway.repository

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.withTransaction
import com.google.android.gms.maps.model.LatLng
import com.tiparo.tripway.AppExecutors
import com.tiparo.tripway.BuildConfig
import com.tiparo.tripway.models.Point
import com.tiparo.tripway.models.Resource
import com.tiparo.tripway.models.Trip
import com.tiparo.tripway.repository.database.PointDao
import com.tiparo.tripway.repository.database.TripDao
import com.tiparo.tripway.repository.database.TripwayDB
import com.tiparo.tripway.repository.network.api.ApiResponse
import com.tiparo.tripway.repository.network.api.ApiSuccessResponse
import com.tiparo.tripway.repository.network.api.services.GoogleMapsServices
import com.tiparo.tripway.repository.network.api.services.ReverseGeocodingResponse
import com.tiparo.tripway.repository.network.api.services.ReverseGeocodingResponse.GeocodingResult
import com.tiparo.tripway.repository.network.api.services.TripsService
import com.tiparo.tripway.utils.FileUtils
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val application: Application,
    private val appExecutors: AppExecutors,
    private val tripDao: TripDao,
    private val pointDao: PointDao,
    private val tripwayDB: TripwayDB,
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

    fun loadMyTrips(): LiveData<Resource<List<TripsService.Trip>>> {
        return object :
            NetworkBoundResource<List<TripsService.Trip>, List<TripsService.Trip>>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<List<TripsService.Trip>>> {
                return tripsService.getOwnTrips()
            }
        }.asLiveData()
    }

    fun reverseGeocode(location: LatLng): LiveData<Resource<GeocodingResult>> {
        return object :
            NetworkBoundResource<GeocodingResult, ReverseGeocodingResponse>(appExecutors) {
            override fun createCall(): LiveData<ApiResponse<ReverseGeocodingResponse>> {
                return googleMapsService.reverseGeocoding(
                    convertLatLng(location),
                    BuildConfig.GOOGLE_MAPS_KEY
                )
            }

            override fun mapDTO(response: ApiSuccessResponse<ReverseGeocodingResponse>): ApiSuccessResponse<GeocodingResult> {
                return ApiSuccessResponse(
                    response.body.results.firstOrNull()
                        ?: GeocodingResult(null, "No information from Google. Try another")
                )
            }
        }.asLiveData()
    }

    fun convertLatLng(location: LatLng) = "${location.latitude},${location.longitude}"

    fun loadMyTripsMock(): LiveData<Resource<List<TripsService.Trip>>> {
        return MutableLiveData(Resource.success(tripsMock))
    }

    suspend fun savePoint(pointOnAdding: Point, tripName: String) =
        withContext(Dispatchers.IO) {
            // We need to do two required actions :
            // FIRST, LOAD IMAGES
            // PUT A LINK TO IMAGE, SAVED in local storage TO POINT
            tripwayDB.withTransaction {
                val savedPhotosUriList = savePickedPhotos(pointOnAdding.photos).filterNotNull()

                pointOnAdding.photos = savedPhotosUriList
                pointOnAdding.name = getPointName(pointOnAdding.location.addressComponents)

                if (pointOnAdding.tripId == null) {
                    val newTrip =
                        Trip(tripName = tripName, firstPointName = pointOnAdding.name, photoUri = pointOnAdding.photos.last())
                    val tripId = tripDao.insertTrip(newTrip)
                    pointOnAdding.tripId = tripId
                }
                pointDao.insertPoint(pointOnAdding)
                tripDao.updateTripByNewPoint(
                    tripId = pointOnAdding.tripId!!,
                    name = pointOnAdding.name,
                    photoUri = pointOnAdding.photos.last()
                )

            }
        }

    /**
     * Here we need to obtain the most precious short_name of location starting with <locality> and ending to <country>
     */

    suspend fun getPointName(addressComponents: List<GeocodingResult.AddressComponent>) = withContext(Dispatchers.Default) {
            val locationTypes = listOf(
                "sublocality_level_2",
                "locality",
                "administrative_area_level_2",
                "administrative_area_level_1",
                "country"
            )

            var result = "?"

            locationTypes.forEach { requiredType ->
                addressComponents.forEach { component ->
                    if (requiredType in component.types) {
                        result = component.short_name
                        return@withContext result
                    }
                }
            }
            result
        }

    //TODO понять как можно избегать инкапсулирования private для тестов
    suspend fun savePickedPhotos(pickedPhotosOnAdding: List<Uri>) =
        withContext(Dispatchers.IO) {
            val deferreds = pickedPhotosOnAdding.map { photoUri ->
                async {
                    FileUtils.copyPhotoFromOuterStorageToApp(photoUri, application)
                }
            }
            deferreds.awaitAll()
        }
}