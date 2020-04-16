package com.tiparo.tripway.di.subcomponents

import com.tiparo.tripway.views.ui.LoginFragment
import dagger.Subcomponent

@Subcomponent
interface LoginComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): LoginComponent
    }

}