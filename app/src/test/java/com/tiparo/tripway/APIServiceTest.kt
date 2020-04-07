package com.tiparo.tripway

import com.tiparo.tripway.models.entities.Error
import com.tiparo.tripway.models.repository.ResourceErrorDAO
import com.tiparo.tripway.models.repository.services.GoogleAuthBackendService
import com.tiparo.tripway.utils.ErrorUtils
import com.tiparo.tripway.utils.InjectorUtils
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class APIServiceTest {

    private var mockWebServer = MockWebServer()

    private lateinit var googleAuthBackendService: GoogleAuthBackendService

    @Before
    fun setup() {
        mockWebServer.start()

        googleAuthBackendService =
            Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GoogleAuthBackendService::class.java)
    }

    @Test
    fun authBackend_InvalidToken() {
        // Assign
        val responseServer = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
            .setBody("""{"error":"INVALID_TOKEN","params":[]}""")

        mockWebServer.enqueue(responseServer)

        // Act
        val response = googleAuthBackendService.authBackend("INVALID_TOKEN_STRING").execute()
        val resourceErrorDAO = ErrorUtils().parseError(response.errorBody(), response.code())

        // Assert
        assertEquals(ResourceErrorDAO(Error("INVALID_TOKEN", 500), listOf()), resourceErrorDAO)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }
}
