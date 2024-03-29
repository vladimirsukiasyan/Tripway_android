package com.tiparo.tripway.posting.domain

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLng
import com.tiparo.tripway.AppExecutors
import com.tiparo.tripway.BuildConfig
import com.tiparo.tripway.discovery.api.dto.DiscoveryInfo
import com.tiparo.tripway.home.api.dto.HomeFeedInfo
import com.tiparo.tripway.models.Point
import com.tiparo.tripway.posting.api.dto.PointApi
import com.tiparo.tripway.repository.NetworkBoundResource
import com.tiparo.tripway.repository.database.Converter
import com.tiparo.tripway.repository.network.api.ApiResponse
import com.tiparo.tripway.repository.network.api.ApiSuccessResponse
import com.tiparo.tripway.repository.network.api.services.GoogleMapsServices
import com.tiparo.tripway.repository.network.api.services.ReverseGeocodingResponse
import com.tiparo.tripway.repository.network.api.services.ReverseGeocodingResponse.GeocodingResult
import com.tiparo.tripway.repository.network.api.services.TripsService
import com.tiparo.tripway.utils.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val application: Application,
    private val appExecutors: AppExecutors,
    private val tripsService: TripsService,
    private val googleMapsService: GoogleMapsServices
) {

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

    fun savePoint(pointOnAdding: Point): Observable<Either<Throwable, TripsService.PointPostResult>> {
        return tripsService.postPoint(pointOnAdding.mapToApiDto())
            .doOnError { er: Throwable -> Timber.e(er.toString()) }
            .flatMap {
                val pointId = it.id!!
                val multipartListBody = pointOnAdding.photos.map { uri ->
                    prepareFilePart(uri)
                }
                tripsService
                    .uploadPhotos(multipartListBody, pointId)
                    .defaultIfEmpty(TripsService.PointPostResult(null))
            }
            .toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(Transformers.neverThrowO())
    }

    private fun prepareFilePart(fileUri: Uri): MultipartBody.Part {
        val compressedByteArray =
            FileUtils.compressImage(fileUri, application, reqWidth = 480, reqHeight = 640)
                ?: throw ClientException(ClientException.Code.ImageCompressionException)

        val type = application.contentResolver.getType(fileUri)!!

        val bodyRequest = compressedByteArray.toRequestBody(type.toMediaTypeOrNull())

        return MultipartBody.Part.createFormData("photos", fileUri.lastPathSegment, bodyRequest)
    }

    private fun createPartFromString(str: String): RequestBody {
        return str.toRequestBody(MultipartBody.FORM)
    }

    private fun Point.mapToApiDto() = PointApi(
        name,
        description,
        tripId,
        tripName,
        location.position.latitude,
        location.position.longitude,
        location.address,
        Converter().addressComponentsToString(location.addressComponents)
    )

    fun homeFeedFirstPageResult(): Observable<Either<Throwable, HomeFeedInfo>> = paginator
        .getFirstPage(Unit)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .toObservable()

    fun homeFeedNextPageResult(): Observable<Either<Throwable, HomeFeedInfo>> = paginator
        .nextPage()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .toObservable()

    private val paginator: RxPaginator<HomeFeedInfo, Unit> = RxPaginator(
        BiFunction { pageParams, anchor ->
            tripsService.getHomeFeedPage(anchor)
        },
        Function { homeFeed: HomeFeedInfo ->
            PageToken(homeFeed.anchor, homeFeed.hasMore ?: false)
        }
    )

    /**
     * Here we need to obtain the most precious short_name of location starting with <locality> and ending to <country>
     */

    suspend fun getPointName(addressComponents: List<GeocodingResult.AddressComponent>) =
        withContext(Dispatchers.Default) {
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

//    //TODO понять как можно избегать инкапсулирования private для тестов
//    suspend fun savePickedPhotos(pickedPhotosOnAdding: List<Uri>) =
//        withContext(Dispatchers.IO) {
//            val deferreds = pickedPhotosOnAdding.map { photoUri ->
//                async {
//                    FileUtils.copyPhotoFromOuterStorageToApp(photoUri, application)
//                }
//            }
//            deferreds.awaitAll()
//        }
}