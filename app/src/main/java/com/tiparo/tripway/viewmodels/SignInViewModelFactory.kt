package com.tiparo.tripway.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tiparo.tripway.models.repository.AuthRepository

/**
 * Factory for creating a [SignInViewModelFactory] with a constructor that takes a [AuthRepository]
 */
class SignInViewModelFactory(
    private val authRepository: AuthRepository,
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SignInViewModel(authRepository, application) as T
    }
}