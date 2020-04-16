package com.tiparo.tripway

import android.app.Application
import com.tiparo.tripway.di.AppComponent
import com.tiparo.tripway.di.DaggerAppComponent

open class BaseApplication : Application() {

    val APP_NAME = "com.tiparo.tripway"

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent.builder().application(this)
            .builder()
            .inject(this)
    }
}