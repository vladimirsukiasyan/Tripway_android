package com.tiparo.tripway.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tiparo.tripway.R
import com.tiparo.tripway.models.Resource
import com.tiparo.tripway.models.Trip
import com.tiparo.tripway.repository.TripsRepository
import com.tiparo.tripway.utils.Event
import javax.inject.Inject

class TripsViewModel @Inject constructor(private val tripsRepository: TripsRepository) :
    ViewModel() {

    private val _items = MediatorLiveData<List<Trip>>().apply { value = listOf() }
    val items: MediatorLiveData<List<Trip>> = _items

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    fun loadTrips() {
        val tripsResult = tripsRepository.loadTrips()
        _items.addSource(tripsResult){
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    _items.value = it.data
                }
                Resource.Status.ERROR -> {
                    _items.value = emptyList()
                    showSnackbarMessage(R.string.loading_trips_error)
                }
                Resource.Status.LOADING -> {
                    //TODO добавить позже прогресс бар ожидания
                }
            }
        }
    }

    private fun showSnackbarMessage(messageResource: Int) {
        _snackbarText.value = Event(messageResource)
    }
}