package com.tiparo.tripway.views.ui

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.tiparo.tripway.AppExecutors
import com.tiparo.tripway.BaseApplication
import com.tiparo.tripway.R
import com.tiparo.tripway.databinding.FragmentPostPointMapBinding
import com.tiparo.tripway.viewmodels.TripsViewModel
import com.tiparo.tripway.views.adapters.TripsListAdapter
import javax.inject.Inject


class PostPointMapFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors
    private lateinit var adapter: TripsListAdapter

    private val tripsViewModel: TripsViewModel by navGraphViewModels(R.id.postPointGraph) {
        viewModelFactory
    }

    private var _binding: FragmentPostPointMapBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private var mMap: GoogleMap? = null
    private var mCameraPosition: CameraPosition? = null

    private lateinit var mPlacesClient: PlacesClient
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var mLocationPermissionGranted = false

    // A default location (Sydney, Australia) and default zoom to use when location permission is not granted.
    private val mDefaultLocation = LatLng(-33.8523341, 151.2106085)
    private val DEFAULT_ZOOM: Float = 15F

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.


    // Keys for storing activity state.
    private val KEY_CAMERA_POSITION = "camera_position"
    private val KEY_LOCATION = "location"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "${this.javaClass.name} :onCreate()")

        // Retrieve location and camera position from saved instance state.
        savedInstanceState?.let {
            mLastKnownLocation = it.getParcelable(KEY_LOCATION)
            mCameraPosition = it.getParcelable(KEY_CAMERA_POSITION)
        }

        Places.initialize(requireActivity().applicationContext, getString(R.string.google_maps_key))
        mPlacesClient = Places.createClient(requireActivity())

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_post_point_map,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAutocompleteMapView()
        initMapView()
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        if (mMap != null) {
            outState.putParcelable(
                KEY_CAMERA_POSITION, mMap!!.cameraPosition
            )
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireActivity().applicationContext as BaseApplication).appComponent.inject(this)
    }

    private fun initMapView() {
        val mapFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { map ->
            mMap = map

            getLocationPermission()

            // Turn on the My Location layer and the related control on the map.
            updateLocationUI()

            // Get the current location of the device and set the position of the map.
            getDeviceLocation()

            mMap?.addMarker(MarkerOptions().position(lastPosition))
        }
    }

    var lastPosition: LatLng
        get() = mLastKnownLocation?.let {
            return LatLng(it.latitude, it.latitude)
        } ?: mDefaultLocation
        set(value) {

        }


    private fun initAutocompleteMapView() {
        val autocompletFragment =
            childFragmentManager.findFragmentById(R.id.map_search) as AutocompleteSupportFragment
        autocompletFragment.setPlaceFields(
            arrayListOf(
                Place.Field.ADDRESS,
                Place.Field.ADDRESS_COMPONENTS,
                Place.Field.ID,
                Place.Field.NAME
            )
        )

        autocompletFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(
                    TAG, """Place: ${place.name}, 
                    |${place.id},
                    |${place.address},
                    |${place.addressComponents}"""
                )
            }

            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status");
            }
        })
        //TODO тут надо сделать restrict области поиска!!!
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                requireContext().applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            );
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    mLocationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    private fun updateLocationUI() {
        mMap?.let { map ->
            try {
                if (mLocationPermissionGranted) {
                    map.isMyLocationEnabled = true
                    map.uiSettings.setMyLocationButtonEnabled(true)
                } else {
                    map.isMyLocationEnabled = false
                    map.uiSettings.isMyLocationButtonEnabled = false
                    lastPosition = mDefaultLocation
                    getLocationPermission()
                }
            } catch (e: SecurityException) {
                Log.e("Exception: %s", e.message ?: "Error while updating location UI")
            }
        }
    }

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                val locationResult = mFusedLocationProviderClient.lastLocation;
                locationResult.addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastPosition = task.result?.let { LatLng(it.latitude, it.longitude) }
                            ?: mDefaultLocation
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.exception);
                        mMap?.uiSettings?.isMyLocationButtonEnabled = false;
                    }

                    mMap?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            lastPosition , DEFAULT_ZOOM
                        )
                    )
                }

            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message ?: "Error while getting device location");
        }
    }
}
