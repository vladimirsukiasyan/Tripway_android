package com.tiparo.tripway.repository.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.tiparo.tripway.models.Trip
import com.tiparo.tripway.models.TripWithPoints

@Dao
abstract class TripDao {
    @Transaction
    @Query("SELECT * FROM Trip WHERE id = :tripId")
    abstract fun getTripWithPoints(tripId: Long): TripWithPoints

    @Query("SELECT * FROM Trip WHERE id = :tripId")
    abstract fun getTripById(tripId: Long): Trip

    @Insert
    abstract fun insertTrip(trip: Trip): Long

    @Query("SELECT * FROM Trip")
    abstract suspend fun getTrips(): List<Trip>
}