package com.example.tamangfood.presentation.ui.mainapp.order.tracking

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentDeliveryTrackingBinding
import com.example.tamangfood.presentation.utils.DefaultLocation
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import androidx.core.graphics.drawable.toBitmap

@AndroidEntryPoint
class DeliveryTrackingFragment : Fragment() {

    private var _binding: FragmentDeliveryTrackingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DeliveryTrackingViewModel by viewModels()
    private val args: DeliveryTrackingFragmentArgs by navArgs()

    private var mapView: MapView? = null
    private var driverMarker: Marker? = null
    private var routePolyline: Polyline? = null

    private var routePoints: List<GeoPoint> = emptyList()

    private lateinit var route_start: GeoPoint
    private lateinit var route_end: GeoPoint

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveryTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))

        val address = getString(R.string.sample_tracking_address)
        binding.tvShippingAddress.text = address

        setPointStartEnd()
        setupOsmdroid()
        setupMapScrollTouch()

        binding.ivBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnReturnHome.setOnClickListener {
            findNavController().popBackStack()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateDriverMarker(state.routeProgress)
                    binding.tvEstimatedMins.text =
                        getString(R.string.mins_format, state.remainingEtaMinutes)
                    binding.tvRemainingKm.text = getString(
                        R.string.distance_km_format,
                        state.remainingDistanceKm
                    )
                    updateTimelineHighlight(state.activeTimelineStep, state.isDelivered)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val streetRoute = OsrmStreetRouteFetcher.fetchDrivingRoute(route_start, route_end)
            val km: Float
            if (streetRoute != null) {
                routePoints = streetRoute.points
                km = (streetRoute.distanceMeters / 1000.0).toFloat()
                routePolyline?.setPoints(routePoints)
            } else {
                routePoints = listOf(route_start, route_end)
                km = (route_start.distanceToAsDouble(route_end) / 1000.0).toFloat()
                routePolyline?.setPoints(routePoints)
            }
            mapView?.invalidate()
            fitBoundsToRoute(routePoints)

            viewModel.initOrder(args.orderId, address, km)
            if (savedInstanceState == null) {
                viewModel.startOrRestartTracking()
            }
        }
    }

    private fun setupOsmdroid() {
        Configuration.getInstance().load(
            requireContext(),
            requireContext().getSharedPreferences("osmdroid", Context.MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = requireContext().packageName

        val mv = binding.mapTracking
        mapView = mv
        mv.setTileSource(TileSourceFactory.MAPNIK)
        mv.setMultiTouchControls(true)
        mv.isHorizontalMapRepetitionEnabled = false
        mv.isVerticalMapRepetitionEnabled = false

        val start = route_start
        val end = route_end

        routePoints = listOf(start, end)
        val polyline = Polyline(mv).apply {
            setPoints(routePoints)
            outlinePaint.color = ContextCompat.getColor(requireContext(), R.color.orange_base)
            outlinePaint.strokeWidth = 2f * resources.displayMetrics.density
        }
        routePolyline = polyline
        mv.overlays.add(polyline)
        // Draw pickup point
        val pickup = Marker(mv).apply {
            val markerIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_address)
            markerIcon?.let {
                setIcon(it)
            }
            position = start
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = getString(R.string.map_point_pickup)
        }
        //Draw dropoff point
        val dropoff = Marker(mv).apply {
            val markerIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_address)
            markerIcon?.let {
                setIcon(it)
            }
            position = end
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = getString(R.string.map_point_dropoff)
        }
        // Draw driver
        val driver = Marker(mv).apply {
            val markerIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_driver)
            markerIcon?.let {
                setIcon(it)
            }
            position = start
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = getString(R.string.map_driver)
        }
        driverMarker = driver

        mv.overlays.add(pickup)
        mv.overlays.add(dropoff)
        mv.overlays.add(driver)

        mv.post {
            fitBoundsToRoute(routePoints)
        }
    }

    private fun fitBoundsToRoute(points: List<GeoPoint>) {
        val mv = mapView ?: return
        if (points.isEmpty()) return
        val north = points.maxOf { it.latitude }
        val south = points.minOf { it.latitude }
        val east = points.maxOf { it.longitude }
        val west = points.minOf { it.longitude }
        val bbox = BoundingBox(north, east, south, west)
        mv.post { mv.zoomToBoundingBox(bbox, true, 100) }
    }

    private fun setupMapScrollTouch() {
        binding.mapTracking.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN ->
                    v.parent.requestDisallowInterceptTouchEvent(true)

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                    v.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
    }

    private fun updateDriverMarker(progress: Float) {
        val mv = mapView ?: return
        val driver = driverMarker ?: return
        val pts = routePoints
        if (pts.isEmpty()) return
        driver.setPosition(positionAlongRoute(pts, progress))
        mv.postInvalidate()
    }

    private fun updateTimelineHighlight(activeStep: Int, delivered: Boolean) {
        val steps = listOf(
            binding.timelineDot3 to binding.timelineLabel3,
            binding.timelineDot4 to binding.timelineLabel4
        )
        steps.forEachIndexed { index, (dot, label) ->
            val on = delivered && index == 3 || !delivered && index == activeStep
            dot.alpha = if (on) 1f else 0.45f
        }
    }

    private fun setPointStartEnd(){
        // TODO: get route start from args
        // Mock data
        route_end = GeoPoint(20.9802749, 105.777174)
        route_start = GeoPoint(DefaultLocation.LAT.value, DefaultLocation.LNG.value)
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        mapView?.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        mapView = null
        driverMarker = null
        routePolyline = null
        super.onDestroyView()
        _binding = null
    }
}
