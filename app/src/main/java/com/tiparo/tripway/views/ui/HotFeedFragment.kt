package com.tiparo.tripway.views.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.tiparo.tripway.AppExecutors
import com.tiparo.tripway.BaseApplication
import com.tiparo.tripway.R
import com.tiparo.tripway.databinding.FragmentHotFeedBinding
import com.tiparo.tripway.databinding.TripItemBinding
import com.tiparo.tripway.utils.setupSnackbar
import com.tiparo.tripway.viewmodels.TripsViewModel
import com.tiparo.tripway.views.adapters.TripsListAdapter
import javax.inject.Inject

class HotFeedFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private val viewModel: TripsViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var adapter: TripsListAdapter

    private lateinit var binding: FragmentHotFeedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_hot_feed,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycleView()
        setupSnackbar()

        viewModel.loadTrips()
    }

    private fun initRecycleView() {
        adapter = TripsListAdapter(
            appExecutors = appExecutors,
            tripClickCallback = {trip ->
                val direction =
                    HotFeedFragmentDirections.actionHotFeedFragmentDestToTripDetailFragment(trip.id)
                findNavController().navigate(direction)
            }
        )
        binding.tripsList.adapter = adapter
        viewModel.items.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().applicationContext as BaseApplication).appComponent.inject(this)
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_LONG)
    }
}
