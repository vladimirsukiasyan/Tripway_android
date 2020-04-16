package com.tiparo.tripway.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tiparo.tripway.viewmodels.SignInViewModel
import com.tiparo.tripway.viewmodels.TripsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SignInViewModel::class)
    abstract fun bindSignInViewModel(signInViewModel: SignInViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TripsViewModel::class)
    abstract fun bindTripsViewModel(tripsViewModel: TripsViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: TripwayViewModelFactory): ViewModelProvider.Factory
}