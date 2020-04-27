package com.tiparo.tripway.views.ui

import android.content.Context
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
import com.tiparo.tripway.BaseApplication
import com.tiparo.tripway.R
import com.tiparo.tripway.databinding.FragmentHotFeedBinding
import com.tiparo.tripway.viewmodels.SignInViewModel
import com.tiparo.tripway.viewmodels.SignInViewModel.SignInState
import kotlinx.android.synthetic.main.fragment_hot_feed.view.*
import javax.inject.Inject

class HotFeedFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val signInViewModel: SignInViewModel by activityViewModels {
        viewModelFactory
    }

    private lateinit var binding: FragmentHotFeedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHotFeedBinding.inflate(inflater)
        val view = binding.root

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().applicationContext as BaseApplication).appComponent.inject(this)
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
