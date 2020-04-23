package com.tiparo.tripway.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.model.Place
import com.tiparo.tripway.models.Resource
import com.tiparo.tripway.repository.TripsRepository
import com.tiparo.tripway.repository.network.api.services.TripsService
import javax.inject.Inject

class TripsViewModel @Inject constructor(private val tripsRepository: TripsRepository) :
    ViewModel() {

    private var selected: TripsService.Trip? = null

    val pickedLocation = MutableLiveData<LatLng>()
    val pickedPlace = MutableLiveData<Place>()

    val locationName = MediatorLiveData<Resource<String>>()
    init {
        locationName.addSource(pickedLocation) { location ->
            val resource = tripsRepository.reverseGeocode(location)
            locationName.addSource(resource) {
                locationName.value = it
            }
        }
        locationName.addSource(pickedPlace) {
            locationName.value = Resource.success(it.name)
        }
    }

    fun selectTripToPost(trip: TripsService.Trip?) {
        selected = trip
    }

    val trips = tripsRepository.loadMyTripsMock()
}