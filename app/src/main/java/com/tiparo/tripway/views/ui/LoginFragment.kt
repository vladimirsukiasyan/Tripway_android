package com.tiparo.tripway.views.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.tiparo.tripway.BaseApplication
import com.tiparo.tripway.R
import com.tiparo.tripway.databinding.FragmentLoginBinding
import com.tiparo.tripway.viewmodels.SignInViewModel
import com.tiparo.tripway.viewmodels.SignInViewModel.SignInState
import kotlinx.android.synthetic.main.fragment_login.view.*
import javax.inject.Inject

private val RC_GET_TOKEN = 1;
val TAG = "Tripway"

class LoginFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val signInViewModel: SignInViewModel by activityViewModels {
        viewModelFactory
    }

    private var _binding: FragmentLoginBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        Log.d(TAG,"LoginFragment onCreateView()")
        _binding = FragmentLoginBinding.inflate(inflater)
        val view = binding.root

        view.sign_in_button.setOnClickListener {
            signInViewModel.authenticationState.value = SignInState.LOADING
            //Call GoogleAuthApi to receive tokenId
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(
                signInIntent,
                RC_GET_TOKEN
            )
        }

        val navController = findNavController()

        signInViewModel.authenticationState.value = SignInState.UNAUTHENTICATED
        signInViewModel.authenticationState.observe(viewLifecycleOwner) {
            when (it) {
                SignInState.AUTHENTICATED -> {
                    hideProgress()
                    navController.popBackStack()
                }
                SignInState.FAILED_AUTHENTICATION -> {
                    hideProgress()
                    Toast.makeText(
                        context,
                        getString(R.string.CANT_AUTH_BACKEND),
                        Toast.LENGTH_LONG
                    ).show()
                }
                SignInState.LOADING -> {
                    showProgress()
                }
                else -> {
                }
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().applicationContext as BaseApplication).appComponent.inject(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GET_TOKEN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            signInViewModel.handleSignInResult(task);
        }
    }

    private fun showProgress() {
        binding.root.signInProgressBar.visibility = View.VISIBLE

    }

    private fun hideProgress() {
        binding.root.signInProgressBar.visibility = View.GONE
    }
}
