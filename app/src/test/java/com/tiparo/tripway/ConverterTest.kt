package com.tiparo.tripway

import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tiparo.tripway.repository.database.Converter
import com.tiparo.tripway.repository.network.api.services.ReverseGeocodingResponse
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class ConverterTest {
    @Test
    fun convertFromString_to_listAddressComponents() {
        val addressComponents = listOf(
            ReverseGeocodingResponse.GeocodingResult.AddressComponent(
                "a",
                "b",
                listOf("city", "locality")
            )
        )

        val convertResult = Gson().toJson(addressComponents)

        Assert.assertEquals(
            convertResult,
            """[{"long_name":"a","short_name":"b","types":["city","locality"]}]"""
        )
    }

    @Test
    fun convertFromAddressComponents_to_String() {
        val addressComponents =
            """[{"long_name":"a","short_name":"b","types":["city","locality"]}]"""

        val listType = object :
            TypeToken<List<ReverseGeocodingResponse.GeocodingResult.AddressComponent>>() {}.type
        val convertResult =
            Gson().fromJson<List<ReverseGeocodingResponse.GeocodingResult.AddressComponent>>(
                addressComponents,
                listType
            )

        Assert.assertEquals(
            convertResult, listOf(
                ReverseGeocodingResponse.GeocodingResult.AddressComponent(
                    "a",
                    "b",
                    listOf("city", "locality")
                )
            )
        )
    }

    @Test
    fun convertFromListPhotosToString_Empty() {
        val pickedPhotosOnAdding = listOf<Uri>()
        val converter = Converter()

        val photosStringConverted = converter.photosUriToString(pickedPhotosOnAdding)

        Assert.assertEquals(
            "",
            photosStringConverted
        )
    }

    @Test
    fun convertFromStringToListPhotos() {
        val photosListInString = ""
        val converter = Converter()
//        Mockito.`when`(Uri.parse(Mockito.any())).thenReturn(Uri.EMPTY)

        val photosList = converter.stringToPhotosUri(photosListInString)

        Assert.assertEquals(
            listOf<Uri>(), photosList
        )
    }
}