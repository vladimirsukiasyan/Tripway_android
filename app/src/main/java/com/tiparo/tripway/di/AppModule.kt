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
import android.content.Context
import com.tiparo.tripway.BaseApplication
import com.tiparo.tripway.repository.network.api.services.GoogleAuthBackendService
import com.tiparo.tripway.repository.network.api.services.TripsService
import com.tiparo.tripway.repository.network.environment.BaseEnvironment
import com.tiparo.tripway.repository.network.environment.Environment
import com.tiparo.tripway.repository.network.http.BaseHttpClient
import com.tiparo.tripway.repository.network.http.HttpClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Singleton
    @Provides
    fun provideGoogleAuthService(httpClient: HttpClient): GoogleAuthBackendService {
        return httpClient.getApiService(GoogleAuthBackendService::class.java)
    }

    @Singleton
    @Provides
    fun provideTripsService(httpClient: HttpClient): TripsService {
        return httpClient.getApiService(TripsService::class.java)
    }

    @Singleton
    @Provides
    fun provideRetrofit(httpClient: HttpClient): Retrofit {
        return httpClient.getRetrofit()
    }

    @Singleton
    @Provides
    fun provideHttpClient(application: Application, environment: Environment): HttpClient {
        return BaseHttpClient(environment, application)
    }

    @Singleton
    @Provides
    fun provideEnvironment(): Environment {
        return BaseEnvironment()
    }
//
//    @Singleton
//    @Provides
//    fun provideDb(app: Application): GithubDb {
//        return Room
//            .databaseBuilder(app, GithubDb::class.java, "github.db")
//            .fallbackToDestructiveMigration()
//            .build()
//    }
//
//    @Singleton
//    @Provides
//    fun provideUserDao(db: GithubDb): UserDao {
//        return db.userDao()
//    }
//
//    @Singleton
//    @Provides
//    fun provideRepoDao(db: GithubDb): RepoDao {
//        return db.repoDao()
//    }
}
