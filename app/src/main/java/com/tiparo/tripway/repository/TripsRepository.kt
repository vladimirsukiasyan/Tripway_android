//package com.tiparo.tripway.repository
//
//import androidx.lifecycle.LiveData
//import com.tiparo.tripway.AppExecutors
//import com.tiparo.tripway.models.Resource
//import com.tiparo.tripway.repository.database.TripDao
//import com.tiparo.tripway.repository.network.api.services.TripsService
//import javax.inject.Inject
//
//class TripsRepository @Inject constructor(
//    private val appExecutors: AppExecutors,
//    private val tripDao: TripDao,
//    private val tripsService: TripsService
////    private val db: TripwayDb
//
//) {
////    fun loadMyTrips(): LiveData<Resource<List<TripsService.Trip>>> {
////        return object :
////            NetworkBoundResource<List<TripsService.Trip>, List<TripsService.Trip>>(appExecutors) {
////            override fun saveCallResult(item: List<TripsService.Trip>) {
////                //tripDao.insertTrips(item)
////            }
////
////            override fun shouldFetch(data: List<TripsService.Trip>?): Boolean {
////                return data == null || data.isEmpty()
////            }
////
////            override fun loadFromDb(): LiveData<List<TripsService.Trip>> {
//////                tripDao.loadTrips(owner)
////            }
////
////            override fun createCall() = tripsService.getOwnTrips()
////
////            override fun onFetchFailed() {
////                //
////            }
////
////        }.asLiveData()
////    }
//}