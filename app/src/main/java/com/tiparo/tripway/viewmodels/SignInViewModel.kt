package com.tiparo.tripway.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.tiparo.tripway.R
import com.tiparo.tripway.models.Resource
import com.tiparo.tripway.repository.AuthRepository
import com.tiparo.tripway.views.ui.TAG
import javax.inject.Inject

class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val applicationContext: Application
) : ViewModel() {

    val authenticationState = MediatorLiveData<SignInState>()

    private val mGoogleSignInClient: GoogleSignInClient

    init {
        authenticationState.value = SignInState.UNAUTHENTICATED

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(applicationContext.getString(R.string.server_client_id))
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(applicationContext, gso)
    }

    private fun authGoogleBackend(idToken: String) {
        val progressAuthLiveData = authRepository.authUser(idToken)
        authenticationState.addSource(progressAuthLiveData) { response ->
            when (response.status) {
                Resource.Status.SUCCESS -> {
                    authenticationState.value = SignInState.AUTHENTICATED
                    authenticationState.removeSource(progressAuthLiveData)
                }
                Resource.Status.LOADING -> {
                    authenticationState.value = SignInState.LOADING
                }
                Resource.Status.ERROR -> {
                    authenticationState.value = SignInState.FAILED_AUTHENTICATION
                    authenticationState.removeSource(progressAuthLiveData)

                    Log.e(
                        TAG,
                        "[ERROR] AuthBackend: error = ${response.message ?: "Unknown error"}"
                    )
                }
            }
        }
    }

    private fun silentGoogleAuth() {
        val task = mGoogleSignInClient.silentSignIn()
        if (task.isSuccessful) {
            // There's immediate result available.
            processSilentAuthResult(task.result?.idToken)
        } else {
            // There's no immediate result ready, displays progress indicator and waits for the
            // async callback.
            authenticationState.value = SignInState.LOADING
            task.addOnCompleteListener {
                try {
                    processSilentAuthResult(it.getResult(ApiException::class.java)?.idToken)
                } catch (exception: ApiException) {
                    //TODO format error output
                    processSilentAuthResult(null)
                }
            }
        }
    }

    private fun processSilentAuthResult(idToken: String?) {
        if (idToken != null) {
            authGoogleBackend(idToken)
        } else {
            authenticationState.value = SignInState.FAILED_AUTHENTICATION
        }
    }

    fun handleSignInResult(task: Task<GoogleSignInAccount>?) {
        try {
            val idToken = task?.result?.idToken
            if (idToken != null) {
                Log.d(TAG, "[TOKEN_ID]${idToken}")

                authGoogleBackend(idToken)
            } else {
                authenticationState.value = SignInState.FAILED_AUTHENTICATION

                Log.e(TAG, "[ERROR] CANT_GET_GOOGLE_AUTH_TOKEN")
            }
        } catch (e: ApiException) {
            authenticationState.value = SignInState.FAILED_AUTHENTICATION
            Log.e(TAG, "[ERROR] GoogleSignIn: error = $e", e)
        }
    }

    enum class SignInState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        LOADING,                // Authentication is loading
        AUTHENTICATED,        // The user has authenticated successfully
        FAILED_AUTHENTICATION  // Authentication failed
    }
}