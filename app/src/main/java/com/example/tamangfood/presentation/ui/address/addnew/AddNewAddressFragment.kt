package com.example.tamangfood.presentation.ui.address.addnew

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.example.tamangfood.R
import com.example.tamangfood.data.model.SearchLocationItem
import com.example.tamangfood.databinding.FragmentAddNewAddressBinding
import com.example.tamangfood.presentation.utils.DefaultLocation
import com.example.tamangfood.presentation.utils.Utils
import com.example.tamangfood.presentation.utils.Zoom
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import java.util.Locale
import kotlin.math.ln

@AndroidEntryPoint
class AddNewAddressFragment : Fragment() {
    private var _binding: FragmentAddNewAddressBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddNewAddressViewModel by viewModels()
    private var mapView: MapView? = null
    private var marker: Marker? = null
    private var selectedLocation: GeoPoint? = null
    private var addressFormBottomSheet: AddressFormBottomSheet? = null
    private var currentLocation: GeoPoint? = null
    private var searchLocationAdapter: SearchLocationAdapter? = null
    
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var lat: Double = DefaultLocation.LAT.value
    private var lng: Double = DefaultLocation.LNG.value
    private val args: AddNewAddressFragmentArgs by navArgs()
    private var isDetails: Boolean = false
    private var curName: String? = null

    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
        isDetails = args.detail
        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Initialize OSMDroid configuration
        Configuration.getInstance().load(
            requireContext(),
            requireContext().getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = requireContext().packageName

        if(isDetails){
            //TODO: get infor address from server
            // Mock data
            lat = DefaultLocation.LAT.value
            lng = DefaultLocation.LNG.value
            val name = "Home"
            curName = name
            val address = "Ha Noi, Viet Nam"
            showAddressFormBottomSheet(GeoPoint(lat, lng), address, name)

        }
        setupMap()
        setupSearchBar()
        setupRecyclerView()
        setupClickListeners()

        if (checkLocationPermissions()) {
            getCurrentLocation()
        } else {
            requestLocationPermissions()
        }
    }

    private fun setupMap() {
        mapView = binding.mapView
        mapView?.setTileSource(TileSourceFactory.MAPNIK)
        mapView?.controller?.setZoom(Zoom.DEFAULT.value)
        mapView?.controller?.setCenter(GeoPoint(lat, lng))
        mapView?.setMultiTouchControls(true)

        // Add click listener to map using overlay
        val mapOverlay = object : Overlay(requireContext()) {
            override fun onSingleTapConfirmed(e: MotionEvent?, mapView: MapView?): Boolean {
                e?.let {
                    val geoPoint = mapView?.projection?.fromPixels(
                        it.x.toInt(),
                        it.y.toInt()
                    ) as? GeoPoint
                    geoPoint?.let { point ->
                        selectedLocation = point
                        updateMarker(point)

                        binding.rvSearchResults.visibility = View.GONE
                        binding.mapView.visibility = View.VISIBLE
                        binding.btnBackToCurrentLocation.visibility = View.VISIBLE
                        getAddressFromLocationAndShowBottomSheet(point)
                    }
                }
                return true
            }
        }
        mapView?.overlays?.add(mapOverlay)
    }

    private fun getCurrentLocation(showBottomSheet: Boolean = false) {
        if (!checkLocationPermissions()) {
            return
        }
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val geoPoint = GeoPoint(it.latitude, it.longitude)
                    currentLocation = GeoPoint(it.latitude, it.longitude)
                    updateMapToLocation(geoPoint)
                    if (showBottomSheet) {
                        getAddressFromLocationAndShowBottomSheet(geoPoint)
                    }
                } ?: run {
                    requestLocationUpdates(showBottomSheet)
                }
            }.addOnFailureListener {
                requestLocationUpdates(showBottomSheet)
            }
        } catch (e: SecurityException) {
            requestLocationPermissions()
        }
    }

    private fun requestLocationUpdates(showBottomSheet: Boolean = false) {
        if (!checkLocationPermissions()) {
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L
        ).apply {
            setMaxUpdateDelayMillis(5000L)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val geoPoint = GeoPoint(location.latitude, location.longitude)
                    updateMapToLocation(geoPoint)
                    if (showBottomSheet) {
                        getAddressFromLocationAndShowBottomSheet(geoPoint)
                    }
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }

    private fun updateMapToLocation(geoPoint: GeoPoint) {
        var curPoint = geoPoint
        if(isDetails) curPoint = GeoPoint(lat, lng)
        mapView?.controller?.setCenter(curPoint)
        mapView?.controller?.setZoom(Zoom.DEFAULT.value)
        selectedLocation = curPoint
        updateMarker(curPoint)
        lat = curPoint.latitude
        lng = curPoint.longitude
    }


    private fun updateMarker(geoPoint: GeoPoint) {
        mapView?.overlays?.remove(marker)
        marker = Marker(mapView).apply {
            position = geoPoint
            val markerIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_address)
            markerIcon?.let {
                setIcon(it)
            }
            setOnMarkerClickListener { marker, mapView ->
                val geoPoint = marker.position

                selectedLocation = geoPoint
                updateMarker(geoPoint)
                getAddressFromLocationAndShowBottomSheet(geoPoint)

                true
            }
        }
        marker?.let { mapView?.overlays?.add(it) }
        mapView?.invalidate()
    }

    private fun getAddressFromLocationAndShowBottomSheet(geoPoint: GeoPoint) {
        lifecycleScope.launch {
            try {
                val address = withContext(Dispatchers.IO) {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    geocoder.getFromLocation(geoPoint.latitude, geoPoint.longitude, 1)
                        ?.firstOrNull()
                }

                val addressText = address?.getAddressLine(0) 
                    ?: "${geoPoint.latitude}, ${geoPoint.longitude}"
                
                showAddressFormBottomSheet(geoPoint, addressText, curName)
            } catch (e: Exception) {
                val addressText = "${geoPoint.latitude}, ${geoPoint.longitude}"
                showAddressFormBottomSheet(geoPoint, addressText, curName)
            }
        }
    }
    
    private fun showAddressFormBottomSheet(location: GeoPoint, address: String, name: String? = null) {
        addressFormBottomSheet?.dismiss()
        
        parentFragmentManager.setFragmentResultListener(
            AddressFormBottomSheet.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, result ->
            // TODO: save new address
        }
        
        addressFormBottomSheet = AddressFormBottomSheet.newInstance(
            location = location,
            address = address,
            isDetails = isDetails,
            name = name ?: ""
        )
        addressFormBottomSheet?.show(parentFragmentManager, AddressFormBottomSheet.TAG)
    }

    // Search recycler view
    private fun setupRecyclerView() {
        searchLocationAdapter = SearchLocationAdapter { item ->
            selectedLocation = item.geoPoint
            mapView?.controller?.setCenter(item.geoPoint)
            mapView?.controller?.setZoom(Zoom.DEFAULT.value)
            updateMarker(item.geoPoint)
            
            binding.rvSearchResults.visibility = View.GONE
            binding.mapView.visibility = View.VISIBLE
            binding.btnBackToCurrentLocation.visibility = View.VISIBLE
            
            showAddressFormBottomSheet(item.geoPoint, item.fullAddress, curName)
        }
        
        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchLocationAdapter
        }
    }

    private fun setupSearchBar() {
        binding.etSearchLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                binding.ivClearSearch.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (s.isNullOrEmpty()) {
                    binding.rvSearchResults.visibility = View.GONE
                    binding.mapView.visibility = View.VISIBLE
                }
            }
        })

        binding.etSearchLocation.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        binding.ivSearchIcon.setOnClickListener {
            performSearch()
        }

        binding.ivClearSearch.setOnClickListener {
            binding.etSearchLocation.setText("")
            binding.etSearchLocation.clearFocus()
            binding.rvSearchResults.visibility = View.GONE
            binding.mapView.visibility = View.VISIBLE
            binding.btnBackToCurrentLocation.visibility = View.VISIBLE
        }
    }

    private fun performSearch() {
        val searchQuery = binding.etSearchLocation.text.toString().trim()
        if (searchQuery.isEmpty()) {
            return
        }

        searchLocation(searchQuery)
    }

    private fun searchLocation(query: String) {
        lifecycleScope.launch {
            try {
                val addresses = withContext(Dispatchers.IO) {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    geocoder.getFromLocationName(query, 5)
                }

                if (addresses.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.location_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.rvSearchResults.visibility = View.GONE
                    binding.mapView.visibility = View.VISIBLE
                    binding.btnBackToCurrentLocation.visibility = View.VISIBLE
                    return@launch
                }

                val searchResults = addresses.map { address ->
                    val geoPoint = GeoPoint(address.latitude, address.longitude)
                    val fullAddress = address.getAddressLine(0) ?: query
                    SearchLocationItem(address, geoPoint, fullAddress)
                }

                searchLocationAdapter?.submitList(searchResults)
                binding.rvSearchResults.visibility = View.VISIBLE
                binding.mapView.visibility = View.GONE
                binding.btnBackToCurrentLocation.visibility = View.GONE

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.location_not_found),
                    Toast.LENGTH_SHORT
                ).show()
                binding.rvSearchResults.visibility = View.GONE
                binding.mapView.visibility = View.VISIBLE
                binding.btnBackToCurrentLocation.visibility = View.VISIBLE
            }
        }
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        
        binding.btnBackToCurrentLocation.setOnClickListener {
            currentLocation?.let { location ->
                updateMapToLocation(location)
            }
        }

        // click marker on map
        marker?.setOnMarkerClickListener { marker, mapView ->
            val geoPoint = marker.position
            selectedLocation = geoPoint
            updateMarker(geoPoint)
            getAddressFromLocationAndShowBottomSheet(geoPoint)
            true
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return locationPermissions.all {
            ContextCompat.checkSelfPermission(
                requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            locationPermissions,
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                getCurrentLocation()
            } else {
                // Set current location
                val defaultLocation = GeoPoint(lat, lng)
                currentLocation = GeoPoint(lat, lng)
                updateMapToLocation(defaultLocation)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.location_permission_required),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
        locationCallback = null
        mapView = null
        marker = null
        _binding = null
    }
}