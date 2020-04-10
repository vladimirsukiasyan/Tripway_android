package com.tiparo.tripway

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.tiparo.tripway.di.InjectorUtils
import com.tiparo.tripway.models.entities.Error
import com.tiparo.tripway.models.repository.services.GoogleAuthBackendService
import com.tiparo.tripway.models.repository.services.HEADER_AUTHORIZATION
import com.tiparo.tripway.models.repository.services.NullOnEmptyConverterFactory
import com.tiparo.tripway.models.repository.services.response.ResourceErrorDAO
import com.tiparo.tripway.utils.ErrorUtils
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
class AuthAPIServiceTest {

    private var mockWebServer = MockWebServer()

    private lateinit var googleAuthBackendService: GoogleAuthBackendService

    @Before
    fun setup() {
        mockWebServer.start()

        googleAuthBackendService = InjectorUtils.createRetrofit(mockWebServer.url("/"))
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
        assertEquals(
            ResourceErrorDAO(
                Error("INVALID_TOKEN", 500),
                listOf()
            ), resourceErrorDAO
        )
    }

    @Test
    fun authBackendSaveToken() {
        // Assign
        val sessionIDTest = "3489f3n-43f03n3-lkssffn8934f"
        val responseServer = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .addHeader(HEADER_AUTHORIZATION, sessionIDTest)


        mockWebServer.enqueue(responseServer)

        // Act
        val response = googleAuthBackendService.authBackend("RANDOM_TOKEN").execute()

        val context = InstrumentationRegistry.getInstrumentation().targetContext

        val preferences =
            context.getSharedPreferences(BaseApplication.APP_NAME, Context.MODE_PRIVATE)

        val sessionID = preferences.getString(HEADER_AUTHORIZATION, null)
        assertEquals(sessionID, sessionIDTest)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }
}
