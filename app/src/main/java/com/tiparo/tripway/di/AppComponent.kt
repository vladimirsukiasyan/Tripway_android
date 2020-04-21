package com.tiparo.tripway.di

import android.app.Application
import com.tiparo.tripway.BaseApplication
import com.tiparo.tripway.views.ui.HotFeedFragment
import com.tiparo.tripway.views.ui.LoginFragment
import com.tiparo.tripway.views.ui.PostPointListFragment
import com.tiparo.tripway.views.ui.PostPointMapFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppSubcomponents::class,
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
    fun inject(fragment: HotFeedFragment)
    fun inject(fragment: PostPointListFragment)
    fun inject(fragment: PostPointMapFragment)
}