package com.tiparo.tripway.viewmodels

import android.net.Uri
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AddressComponents
import com.google.android.libraries.places.api.model.Place
import com.tiparo.tripway.R
import com.tiparo.tripway.models.Point
import com.tiparo.tripway.models.Resource
import com.tiparo.tripway.models.Trip
import com.tiparo.tripway.repository.PostRepository
import com.tiparo.tripway.repository.TripsRepository
import com.tiparo.tripway.repository.network.api.services.ReverseGeocodingResponse.GeocodingResult
import com.tiparo.tripway.repository.network.api.services.TripsService
import com.tiparo.tripway.utils.Event
import kotlinx.coroutines.launch
import javax.inject.Inject

class PostPointViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val tripsRepository: TripsRepository
) : ViewModel() {

    private val _items = MutableLiveData<List<Trip>>().apply { value = listOf() }
    val items: LiveData<List<Trip>> = _items

    private val pointOnAdding = Point()
    private var pickedPhotosOnAdding: List<Uri> = arrayListOf()

    // Two-way databinding, exposing MutableLiveData
    val description = MutableLiveData<String>()
    val tripName = MutableLiveData<String>()
    var isNewPoint = false

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    val pickedLocation = MutableLiveData<LatLng>()
    val pickedPlace = MutableLiveData<Place>()

    val locationName = MediatorLiveData<Resource<String>>().apply {
        addSource(pickedLocation) { position ->
            //TODO вынести в отдельный обзервер для удобочитаемости
            val resource = postRepository.reverseGeocode(position)
            addSource(resource) {
                saveGeocodingResults(position, it.data)

                value = Resource.success(it.data?.formatted_address)
                //TODO обработать случай, когда data = null (когда Google возвращает Empty Body)
            }
        }
        addSource(pickedPlace) { place ->
            savePlace(place)

            value = Resource.success(place.name)
        }
    }

    private fun savePlace(place: Place) {
        with(pointOnAdding.location) {
            place.latLng?.let { position = it }
            place.address?.let { address = it }
            place.addressComponents?.let { addressComponents = it.mapToLocalAddressComponent() }
        }
    }

    private fun saveGeocodingResults(
        pickedPosition: LatLng,
        results: GeocodingResult?
    ) {
        if (results?.address_components != null) {
            with(pointOnAdding.location) {
                position = pickedPosition
                address = results.formatted_address
                addressComponents = results.address_components
            }
        }
    }

    fun selectTripToPost(trip: Trip?) {
        pointOnAdding.tripId = trip?.id
        isNewPoint = trip == null
    }

    fun savePickedPhotos(obtainResult: List<Uri>) {
        pickedPhotosOnAdding = obtainResult
    }

    val trips = postRepository.loadMyTripsMock()

    fun savePoint() {
        val description = description.value
        if (description.isNullOrBlank()) {
            _snackbarText.value = Event(R.string.empty_description_post_message)
            return
        }
        val tripName = tripName.value
        if (tripName.isNullOrBlank()) {
            _snackbarText.value = Event(R.string.empty_trip_name_post_message)
            return
        }
        pointOnAdding.description = description
        pointOnAdding.photos = pickedPhotosOnAdding

        createPoint(pointOnAdding, tripName)
    }

    private fun createPoint(pointOnAdding: Point, tripName: String) =
        viewModelScope.launch {
            postRepository.savePoint(pointOnAdding, tripName)
        }

    fun loadTrips() {
        //TODO добавить позже прогресс бар ожидания в виде
        // _dataLoading.value = true в начале и _dataLoading.value = false в конце
        viewModelScope.launch {
            val tripsResult = tripsRepository.loadTrips()
            when (tripsResult.status) {
                Resource.Status.SUCCESS -> {
                    _items.value = tripsResult.data
                }
                Resource.Status.ERROR -> {
                    _items.value = emptyList()
                    showSnackbarMessage(R.string.loading_trips_error)
                }
                else -> {
                }
            }
        }
    }

    private fun showSnackbarMessage(messageResource: Int) {
        _snackbarText.value = Event(messageResource)
    }
}


private fun AddressComponents.mapToLocalAddressComponent(): List<GeocodingResult.AddressComponent> =
    asList().map {
        GeocodingResult.AddressComponent(it.name, it.shortName ?: it.name, it.types)
    }

