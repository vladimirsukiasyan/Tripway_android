package com.tiparo.tripway.views.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.tiparo.tripway.BaseApplication
import com.tiparo.tripway.R
import com.tiparo.tripway.databinding.FragmentDetailTripBinding
import com.tiparo.tripway.utils.setupSnackbar
import com.tiparo.tripway.viewmodels.TripDetailViewModel
import com.tiparo.tripway.views.adapters.TripDetailPointPagerAdapter
import timber.log.Timber
import javax.inject.Inject

class TripDetailFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: TripDetailViewModel by viewModels {
        viewModelFactory
    }

    private val args: TripDetailFragmentArgs by navArgs()

    private var mMap: GoogleMap? = null
    private val DEFAULT_ZOOM: Float = 12F
    private val WORLD_ZOOM: Float = 2F

    private lateinit var binding: FragmentDetailTripBinding
    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadTripWithPoints(args.tripId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_detail_trip,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        setupViewPager()
        setupMapView()
        setupSnackbar()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().applicationContext as BaseApplication).appComponent.inject(this)
    }

    private fun setupMapView() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            mMap = map

            viewModel.locationsItems.observe(viewLifecycleOwner) { locations ->
                moveCamera(locations.first(), WORLD_ZOOM)
                locations.forEach {
                    addMarkerOnMap(it)
                }
            }
        }
        val layoutParams = binding.mapViewToolbar.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = AppBarLayout.Behavior()
        behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return false
            }
        })
        layoutParams.behavior = behavior
    }

    private fun setupViewPager() {
        viewModel.pointsList.observe(viewLifecycleOwner){
            binding.pointPager.adapter = TripDetailPointPagerAdapter(this, it.size)
            binding.pointPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    //TODO изменять позицию камеры
                    val point = viewModel.pointsList.value!![position]
                    moveCamera(point.location.position)

                    Timber.d("ViewPager: position = $position")
                }
            })
        }
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_LONG)
    }

    private fun moveCamera(newPosition: LatLng, zoom: Float = DEFAULT_ZOOM) {
        mMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                newPosition, zoom
            )
        )
    }

    private fun addMarkerOnMap(newPosition: LatLng) {
        mMap?.addMarker(
            MarkerOptions()
                .position(newPosition)
                .draggable(false)
        )
    }
}
