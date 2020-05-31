package com.tiparo.tripway.viewmodels

import android.net.Uri
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.tiparo.tripway.R
import com.tiparo.tripway.models.Point
import com.tiparo.tripway.models.Resource
import com.tiparo.tripway.models.Trip
import com.tiparo.tripway.models.TripWithPoints
import com.tiparo.tripway.repository.TripsRepository
import com.tiparo.tripway.utils.Event
import kotlinx.coroutines.launch
import javax.inject.Inject

class TripDetailViewModel @Inject constructor(private val tripsRepository: TripsRepository) :
    ViewModel() {
    private var tripWithPoint: TripWithPoints? = null

    val _locationsItems = MutableLiveData<List<LatLng>>()
    val locationsItems: LiveData<List<LatLng>> = _locationsItems

    private val _pointsList = MutableLiveData<List<Point>>()
    val pointsList: LiveData<List<Point>> = _pointsList

    private val _tripRoute = MutableLiveData<String>()
    val tripRoute: LiveData<String> = _tripRoute

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _deletedEvent = MutableLiveData<Event<Unit>>()
    val deletedEvent: LiveData<Event<Unit>> = _deletedEvent

    fun loadTripWithPoints(tripId: Long) {
        viewModelScope.launch {
            val tripWithPointsResource = tripsRepository.loadTripWithPoints(tripId)
            when (tripWithPointsResource.status) {
                Resource.Status.SUCCESS -> {
                    tripWithPointsResource.data?.let {
                        tripWithPoint = it

                        _pointsList.value = it.points
                        setTripRoute(it.trip)
                        setLocations(it.points)
                        //TODO set description method
                    }
                }
                Resource.Status.ERROR -> {
                    showSnackbarMessage(R.string.loading_trip_error)
                }
                else -> {
                }
            }
        }
    }

    private fun setTripRoute(trip: Trip) {
        _tripRoute.value = "${trip.firstPointName} > ${trip.lastPointName}"
    }

    private fun setLocations(points: List<Point>) {
        _locationsItems.value = points.map { it.location.position }
    }

    fun getPointName(position: Int): LiveData<String> = Transformations.map(pointsList) {
        it[position].name
    }

    fun getPointDescription(position: Int): LiveData<String> = Transformations.map(pointsList) {
        it[position].description
    }

    fun getPointPhotos(position: Int): LiveData<List<Uri>> = Transformations.map(pointsList) {
        it[position].photos
    }

    fun deleteTrip() {
        viewModelScope.launch {
            tripWithPoint?.let {
                val result = tripsRepository.deleteTrip(it.trip.id)
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        _deletedEvent.value = Event(Unit)
                    }
                    else -> {
                        showSnackbarMessage(R.string.delete_trip_failed)
                    }
                }
            }
        }
    }

    fun deletePoint(pointPosition: Int) {
        viewModelScope.launch {
            tripWithPoint?.let {
                val result = tripsRepository.deletePoint(it, pointPosition)
                when (result.status) {
                    Resource.Status.SUCCESS -> {
                        _deletedEvent.value = Event(Unit)
                    }
                    else -> {
                        showSnackbarMessage(R.string.delete_point_failed)
                    }
                }
            }
        }
    }

    private fun showSnackbarMessage(messageResource: Int) {
        _snackbarText.value = Event(messageResource)
    }
}