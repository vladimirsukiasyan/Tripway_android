package com.tiparo.tripway.repository.database

import android.net.Uri
import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tiparo.tripway.repository.network.api.services.ReverseGeocodingResponse.GeocodingResult.AddressComponent

class Converter {
    @TypeConverter
    fun latLngToString(latLng: LatLng) =
        "${latLng.latitude},${latLng.longitude}"

    @TypeConverter
    fun stringToLatLng(latLng: String): LatLng {
        val (lat, lng) = latLng.split(",").map { it.toDouble() }
        return LatLng(lat, lng)
    }

    @TypeConverter
    fun addressComponentsToSting(addressComponents: List<AddressComponent>): String =
        Gson().toJson(addressComponents)


    @TypeConverter
    fun stringToAddressComponents(addressComponents: String): List<AddressComponent> {
        val listType = object : TypeToken<ArrayList<AddressComponent>>() {}.type
        return Gson().fromJson(addressComponents, listType)
    }

    @TypeConverter
    fun photosUriToString(photos: List<Uri>): String {
        val jsonPhotos = Gson().toJson(photos)
        return if (jsonPhotos == "[]") ""
        else jsonPhotos
    }

    @TypeConverter
    fun stringToPhotosUri(photosUri: String): List<Uri> {
        val listType = object : TypeToken<ArrayList<Uri>>() {}.type
        //if photosUri is empty, fromGson() will return null and we need to replace it with emptyList
        return Gson().fromJson(photosUri, listType) ?: emptyList()
    }
}