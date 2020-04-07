package com.tiparo.tripway.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.tiparo.tripway.R
import com.tiparo.tripway.models.repository.AuthRepository
import com.tiparo.tripway.models.repository.Resource
import com.tiparo.tripway.views.ui.TAG

class SignInViewModel(
    private val authRepository: AuthRepository,
    applicationContext: Application
) : AndroidViewModel(applicationContext) {

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
                }
                Resource.Status.LOADING -> {
                    authenticationState.value = SignInState.LOADING
                }
                Resource.Status.ERROR -> {
                    authenticationState.value = SignInState.FAILED_AUTHENTICATION

                    Log.e(
                        TAG,
                        "[ERROR] AuthBackend: error = ${response.resourceError?.error
                            ?: "Unknown error"}"
                    )
                }
            }
        }
    }

    fun checkAuth() {
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
                    processSilentAuthResult(null, exception.statusCode.toString())
                }
            }
        }
    }

    private fun processSilentAuthResult(idToken: String?, error: String = "") {
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
                authGoogleBackend(idToken)
            } else {
                authenticationState.value = SignInState.FAILED_AUTHENTICATION

                Log.w(TAG, "[ERROR] CANT_GET_GOOGLE_AUTH_TOKEN")
            }
        } catch (e: ApiException) {
            authenticationState.value = SignInState.FAILED_AUTHENTICATION
            Log.w(TAG, "[ERROR] GoogleSignIn: error = $e", e)
        }
    }

    enum class SignInState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        LOADING,                // Authentication is loading
        AUTHENTICATED,        // The user has authenticated successfully
        FAILED_AUTHENTICATION  // Authentication failed
    }
}