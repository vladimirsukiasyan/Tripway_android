package com.tiparo.tripway.di

import android.app.Application
import com.tiparo.tripway.BaseApplication
import com.tiparo.tripway.views.ui.*
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class
    ]
)
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun builder(): AppComponent
    }

    fun inject(baseApplication: BaseApplication)

    fun inject(fragment: LoginFragment)
    fun inject(fragment: HomeFragment)
    fun inject(fragment: TripDetailFragment)
    fun inject(fragment: PointFragment)
    fun inject(fragment: PostPointListFragment)
    fun inject(fragment: PostPointMapFragment)
    fun inject(fragment: PostPointPhotosFragment)
    fun inject(fragment: PostPointDescriptionFragment)
}