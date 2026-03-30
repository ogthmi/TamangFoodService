package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.address.addnew

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import com.example.tamangfood.domain.model.Address
import com.example.tamangfood.databinding.FragmentAddNewAddressBinding
import com.example.tamangfood.presentation.utils.AppPreferences
import com.example.tamangfood.presentation.utils.DefaultLocation
import com.example.tamangfood.presentation.utils.NetworkState
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
    private val TAG = "ADD_NEW_ADDRESS"
    
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var lat: Double = DefaultLocation.LAT.value
    private var lng: Double = DefaultLocation.LNG.value
    private val args: AddNewAddressFragmentArgs by navArgs()
    private var isDetails: Boolean = false
    private var curName: String? = null
    private var isClickBackToCurrentButton: Boolean = false

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

        if (isDetails && args.id > 0) {
            binding.tvTitle.text = "My Address"
            viewModel.loadAddressById(args.id)
        } else if (isDetails) {
            binding.tvTitle.text = "My Address"
            Utils.showToast(requireContext(), "Invalid address id")
            findNavController().popBackStack()
            return
        }
        setupMap()
        setupSearchBar()
        setupRecyclerView()
        setupClickListeners()

        // Luôn check permission -> lấy vị trí hiện tại đang đứng
        if (checkLocationPermissions()) {
            getCurrentLocation()
        } else {
            requestLocationPermissions()
        }

        // Nhận lại action từ bottom sheet
        parentFragmentManager.setFragmentResultListener(
            AddressFormBottomSheet.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val lat = bundle.getDouble(AddressFormBottomSheet.RESULT_LATITUDE, 0.0)
            val lng = bundle.getDouble(AddressFormBottomSheet.RESULT_LONGITUDE, 0.0)
            val name = bundle.getString(AddressFormBottomSheet.RESULT_NAME).orEmpty()

            val action = bundle.getString(AddressFormBottomSheet.RESULT_ACTION).orEmpty()
            val actionAddressId =
                bundle.getInt(AddressFormBottomSheet.RESULT_ADDRESS_ID, args.id)

            when (action) {
                AddressFormBottomSheet.ACTION_DELETE -> {
                    showPopupConfirm(
                        "Delete address",
                        "Are you sure you want to delete the address?",
                        "Delete"){
                        viewModel.deleteAddress(actionAddressId)
                    }
                }
                AddressFormBottomSheet.ACTION_UPDATE -> {
                    showPopupConfirm(
                        "Update address",
                        "Are you sure you want to update the address?",
                        "Update"){
                        viewModel.updateAddress(actionAddressId, name, lat, lng)
                    }
                }
                else -> viewModel.addAddress(name, lat, lng)
            }
        }

        observeAddAddress()
        observeUpdateAddress()
        observeDeleteAddress()
        observeAddressById()
    }

    private fun observeAddressById() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addressByIdState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> Unit
                        is NetworkState.Success<*> -> {
                            val address = state.data as Address
                            lat = address.latitude
                            lng = address.longitude
                            curName = address.name
                            val geo = GeoPoint(lat, lng)
                            updateMapToLocation(geo)
                            getAddressFromLocationAndShowBottomSheet(geo)
                            viewModel.resetAddressByIdState()
                        }
                        is NetworkState.Error -> {
                            Utils.showToast(requireContext(), state.message)
                            viewModel.resetAddressByIdState()
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun observeAddAddress() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addAddressState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> Unit
                        is NetworkState.Success<*> -> {
                            Utils.showToast(requireContext(), "Add address successful!")
                            viewModel.resetAddAddressState()
                            findNavController().popBackStack()
                        }
                        is NetworkState.Error -> {
                            Utils.showToast(requireContext(), state.message)
                            viewModel.resetAddAddressState()
                        }
                    }
                }
            }
        }
    }

    private fun observeUpdateAddress() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateAddressState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> Unit
                        is NetworkState.Success<*> -> {
                            Utils.showToast(requireContext(), "Update address successful!")
                            viewModel.resetUpdateAddressState()
                            findNavController().popBackStack()
                        }
                        is NetworkState.Error -> {
                            Utils.showToast(requireContext(), state.message)
                            viewModel.resetUpdateAddressState()
                        }
                    }
                }
            }
        }
    }

    private fun observeDeleteAddress() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteAddressState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> Unit
                        is NetworkState.Success<*> -> {
                            Utils.showToast(requireContext(), "Delete address successful!")
                            viewModel.resetDeleteAddressState()
                            findNavController().popBackStack()
                        }
                        is NetworkState.Error -> {
                            Utils.showToast(requireContext(), state.message)
                            viewModel.resetDeleteAddressState()
                        }
                    }
                }
            }
        }
    }

    // Init map
    private fun setupMap() {
        mapView = binding.mapView
        mapView?.setTileSource(TileSourceFactory.MAPNIK)
        mapView?.controller?.setZoom(Zoom.DEFAULT.value)
        mapView?.controller?.setCenter(GeoPoint(lat, lng)) // Map được set với vị trí lat, lng
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
                        Log.d(TAG, "lat: ${point.latitude}, long: ${point.longitude}" )
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

    // Hàm lấy vị trí hiện tại đang đứng
    private fun getCurrentLocation(showBottomSheet: Boolean = false) {
        if (!checkLocationPermissions()) {
            return
        }
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val geoPoint = GeoPoint(it.latitude, it.longitude)
                    Log.d(TAG, "Current address: ${it.latitude}, ${it.longitude}")
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
        if(isClickBackToCurrentButton) curPoint = geoPoint
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

        addressFormBottomSheet = AddressFormBottomSheet.newInstance(
            location = location,
            address = address,
            isDetails = isDetails,
            name = name ?: "",
            addressId = args.id
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
                isClickBackToCurrentButton = true
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

    private fun showPopupConfirm(
        title: String,
        des: String,
        confirmButton: String,
        onConfirm: () -> Unit
    ){
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(des)
            .setNegativeButton(R.string.delete_account_cancel) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(confirmButton) { dialog, _ ->
                dialog.dismiss()
                onConfirm()
            }
            .show()
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