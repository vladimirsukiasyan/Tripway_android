package com.tiparo.tripway

import android.app.Application

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        singleton = this
    }

    companion object {
        // Singleton Instance
        private lateinit var singleton: BaseApplication

        const val APP_NAME = "com.tiparo.tripway"

        fun getInstance(): BaseApplication {
            return singleton
        }
    }

}