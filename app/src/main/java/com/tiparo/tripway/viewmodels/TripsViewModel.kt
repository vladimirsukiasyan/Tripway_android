package com.tiparo.tripway.viewmodels

import android.net.Uri
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AddressComponents
import com.google.android.libraries.places.api.model.Place
import com.tiparo.tripway.R
import com.tiparo.tripway.models.Point
import com.tiparo.tripway.models.Resource
import com.tiparo.tripway.repository.PostRepository
import com.tiparo.tripway.repository.network.api.services.ReverseGeocodingResponse.GeocodingResult
import com.tiparo.tripway.repository.network.api.services.TripsService
import com.tiparo.tripway.utils.Event
import kotlinx.coroutines.launch
import javax.inject.Inject

class TripsViewModel @Inject constructor(private val postRepository: PostRepository) :
    ViewModel() {

//    //TODO сделать отдельный viewModel для добавления
//
//    private val pointOnAdding = Point()
//    private var pickedPhotosOnAdding: List<Uri> = listOf()
//
//    // Two-way databinding, exposing MutableLiveData
//    val description = MutableLiveData<String>()
//
//    private val _snackbarText = MutableLiveData<Event<Int>>()
//    val snackbarText: LiveData<Event<Int>> = _snackbarText
//
//    val pickedLocation = MutableLiveData<LatLng>()
//    val pickedPlace = MutableLiveData<Place>()
//
//    val locationName = MediatorLiveData<Resource<String>>().apply {
//        addSource(pickedLocation) { position ->
//            //TODO вынести в отдельный обзервер для удобочитаемости
//            val resource = postRepository.reverseGeocode(position)
//            addSource(resource) {
//                saveGeocodingResults(position, it.data)
//
//                value = Resource.success(it.data?.formatted_address)
//                //TODO обработать случай, когда data = null (когда Google возвращает Empty Body)
//            }
//        }
//        addSource(pickedPlace) { place ->
//            savePlace(place)
//
//            value = Resource.success(place.name)
//        }
//    }
//
//    private fun savePlace(place: Place) {
//        with(pointOnAdding.location) {
//            place.latLng?.let { position = it }
//            place.address?.let { address = it }
//            place.addressComponents?.let { addressComponents = it.mapToLocalAddressComponent() }
//        }
//    }
//
//    private fun saveGeocodingResults(
//        pickedPosition: LatLng,
//        results: GeocodingResult?
//    ) {
//        if (results?.address_components != null) {
//            with(pointOnAdding.location) {
//                position = pickedPosition
//                address = results.formatted_address
//                addressComponents = results.address_components
//            }
//        }
//    }
//
//    fun selectTripToPost(trip: TripsService.Trip?) {
//        pointOnAdding.tripId = trip?.id
//    }
//
//    fun savePickedPhotos(obtainResult: List<Uri>) {
//        pickedPhotosOnAdding = obtainResult
//    }
//
//    val trips = postRepository.loadMyTripsMock()
//
//    fun savePoint() {
//        val description = description.value
//        if (description.isNullOrBlank()) {
//            _snackbarText.value = Event(R.string.empty_description_post_message)
//            return
//        }
//        pointOnAdding.description = description
//
//        createPoint(pointOnAdding, pickedPhotosOnAdding)
//    }
//
//    private fun createPoint(pointOnAdding: Point, pickedPhotosOnAdding: List<Uri>) =
//        viewModelScope.launch {
//            postRepository.savePoint(pointOnAdding, pickedPhotosOnAdding)
//        }
//}
//
//private fun AddressComponents.mapToLocalAddressComponent(): List<GeocodingResult.AddressComponent> =
//    asList().map {
//        GeocodingResult.AddressComponent(it.name, it.shortName ?: it.name, it.types)
//    }
}