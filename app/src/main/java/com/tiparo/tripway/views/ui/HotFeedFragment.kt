package com.tiparo.tripway.views.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.tiparo.tripway.R
import com.tiparo.tripway.databinding.FragmentHotFeedBinding
import com.tiparo.tripway.utils.InjectorUtils
import com.tiparo.tripway.viewmodels.SignInViewModel
import com.tiparo.tripway.viewmodels.SignInViewModel.SignInState
import kotlinx.android.synthetic.main.fragment_hot_feed.view.*

class HotFeedFragment : Fragment() {
    private val signInViewModel: SignInViewModel by activityViewModels {
        InjectorUtils.provideSignInViewModelFactory(requireActivity().application)
    }

    private var _binding: FragmentHotFeedBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signInViewModel.checkAuth()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHotFeedBinding.inflate(inflater)
        val view = binding.root

        val navController = findNavController()

        signInViewModel.authenticationState.observe(viewLifecycleOwner) { authenticationState ->
            when (authenticationState) {
                SignInState.AUTHENTICATED -> {
                    showWelcome()
                }
                SignInState.FAILED_AUTHENTICATION -> {
                    hideProgress()
                    navController.navigate(R.id.action_hotFeedFragment_to_loginFragment)
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

    private fun showProgress() {
        binding.root.signInProgressBar.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.root.signInProgressBar.visibility = View.GONE
    }

    private fun showWelcome() {
        Toast.makeText(context, R.string.SUCCESS_SIGNIN, Toast.LENGTH_LONG).show()
    }

}
