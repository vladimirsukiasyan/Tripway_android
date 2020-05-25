/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tiparo.tripway.di

import android.app.Application
import androidx.room.Room
import com.tiparo.tripway.BuildConfig
import com.tiparo.tripway.repository.database.PointDao
import com.tiparo.tripway.repository.database.TripDao
import com.tiparo.tripway.repository.database.TripwayDB
import com.tiparo.tripway.repository.network.api.services.AuthService
import com.tiparo.tripway.repository.network.api.services.GoogleMapsServices
import com.tiparo.tripway.repository.network.api.services.TripsService
import com.tiparo.tripway.repository.network.http.BaseHttpClient
import com.tiparo.tripway.repository.network.http.HttpClient
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class GoogleMapsHTTPClient

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class TripwayHTTPClient

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Singleton
    @Provides
    fun provideAuthService(@TripwayHTTPClient httpClient: HttpClient): AuthService {
        return httpClient.getApiService(AuthService::class.java)
    }

    @Singleton
    @Provides
    fun provideGoogleService(@GoogleMapsHTTPClient httpClient: HttpClient): GoogleMapsServices {
        return httpClient.getApiService(GoogleMapsServices::class.java)
    }

    @Singleton
    @Provides
    fun provideTripsService(@TripwayHTTPClient httpClient: HttpClient): TripsService {
        return httpClient.getApiService(TripsService::class.java)
    }

    @TripwayHTTPClient
    @Singleton
    @Provides
    fun provideTripwayHttpClient(application: Application): HttpClient {
        return BaseHttpClient(BuildConfig.BASE_URL, application)
    }

    @GoogleMapsHTTPClient
    @Singleton
    @Provides
    fun provideGoogleMapsHttpClient(application: Application): HttpClient {
        return BaseHttpClient(BuildConfig.GOOGLE_MAPS_URL, application)
    }


    @Singleton
    @Provides
    fun provideDb(app: Application): TripwayDB {
        return Room
            .databaseBuilder(app, TripwayDB::class.java, "tripway.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providePointDao(db: TripwayDB): PointDao {
        return db.pointDao()
    }

    @Singleton
    @Provides
    fun provideTripDao(db: TripwayDB): TripDao {
        return db.tripDao()
    }
}
