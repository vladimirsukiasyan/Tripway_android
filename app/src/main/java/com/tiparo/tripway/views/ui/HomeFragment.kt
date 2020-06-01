package com.tiparo.tripway.views.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tiparo.tripway.AppExecutors
import com.tiparo.tripway.BaseApplication
import com.tiparo.tripway.R
import com.tiparo.tripway.databinding.FragmentHomeBinding
import com.tiparo.tripway.utils.setupSnackbar
import com.tiparo.tripway.viewmodels.TripsViewModel
import com.tiparo.tripway.views.adapters.TripsListAdapter
import javax.inject.Inject

class HomeFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    private val viewModel: TripsViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var adapter: TripsListAdapter

    private lateinit var binding: FragmentHomeBinding

    private val args: HomeFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home,
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
            tripClickCallback = { trip ->
                val direction =
                    HomeFragmentDirections.actionHomeFragmentDestToTripDetailFragment(trip.id)
                findNavController().navigate(direction)
            }
        )
        binding.tripsList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.tripsList.adapter = adapter
        viewModel.filteredItems.observe(viewLifecycleOwner) {
            if(it.isNotEmpty()){
                binding.tripsList.visibility = View.VISIBLE
                binding.emptyViewContent.visibility = View.GONE
                adapter.submitList(it)
            }
            else{
                binding.tripsList.visibility = View.GONE
                binding.emptyViewContent.visibility = View.VISIBLE
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().applicationContext as BaseApplication).appComponent.inject(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)

        val searchItem = menu.findItem(R.id.search)
        setupSearchView(searchItem)

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(viewLifecycleOwner, viewModel.snackbarText, Snackbar.LENGTH_LONG)
        arguments?.let {
            if (args.userMessage != 0) {
                viewModel.showSnackbarMessage(args.userMessage)
            }
        }
    }

    private fun setupSearchView(searchItem: MenuItem) {
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                (item?.actionView as SearchView).requestFocus()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                viewModel.clearSearchingInfo()
                return true
            }
        })

        val searchView = searchItem.actionView as SearchView
        searchView.isIconified = false
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.query.value = newText
                return false
            }
        })

    }
}
