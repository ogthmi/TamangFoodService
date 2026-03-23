package com.example.tamangfood.presentation.ui.mainapp.order.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.presentation.ui.mainapp.order.tracking.DeliveryTrackingViewModel.Companion.TOTAL_DISTANCE_KM
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

data class DeliveryTrackingUiState(
    val orderId: Int = 0,
    val shippingAddress: String = "",
    /** 0f..1f along the simulated route */
    val routeProgress: Float = 0.45f,
    /** Minutes left until arrival (simulated from ETA) */
    val remainingEtaMinutes: Int = 25,
    /** Kilometers left */
    val remainingDistanceKm: Float = 3.8f,
    /** Full route length (from simulated start → end on map) */
    val totalRouteKm: Float = TOTAL_DISTANCE_KM,
    val isTracking: Boolean = false,
    val isDelivered: Boolean = false,
    /** 0..3 — which timeline step is emphasized while moving */
    val activeTimelineStep: Int = 2
)

@HiltViewModel
class DeliveryTrackingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DeliveryTrackingUiState())
    val uiState: StateFlow<DeliveryTrackingUiState> = _uiState.asStateFlow()

    private var trackingJob: Job? = null

    fun initOrder(orderId: Int, address: String, totalRouteKm: Float = TOTAL_DISTANCE_KM) {
        _uiState.update {
            it.copy(
                orderId = orderId,
                shippingAddress = address,
                totalRouteKm = totalRouteKm,
                routeProgress = 0f,
                remainingEtaMinutes = INITIAL_ETA_MINUTES,
                remainingDistanceKm = totalRouteKm,
                isTracking = false,
                isDelivered = false,
                activeTimelineStep = 2
            )
        }
    }

    fun startOrRestartTracking() {
        trackingJob?.cancel()
        val totalKm = _uiState.value.totalRouteKm
        _uiState.update {
            it.copy(
                routeProgress = 0f,
                remainingEtaMinutes = INITIAL_ETA_MINUTES,
                remainingDistanceKm = totalKm,
                isTracking = true,
                isDelivered = false,
                activeTimelineStep = 2
            )
        }
        trackingJob = viewModelScope.launch {
            val steps = SIMULATION_STEPS
            repeat(steps) { i ->
                val p = (i + 1) / steps.toFloat()
                val remainingMin = max(0, ((1f - p) * INITIAL_ETA_MINUTES).roundToInt())
                val remainingKm = (1f - p) * totalKm
                val step = when {
                    p < 1f -> 0
                    else -> 1
                }
                _uiState.update {
                    it.copy(
                        routeProgress = p,
                        remainingEtaMinutes = remainingMin,
                        remainingDistanceKm = remainingKm,
                        activeTimelineStep = step
                    )
                }
                delay(SIMULATION_STEP_DELAY_MS)
            }
            _uiState.update {
                it.copy(
                    routeProgress = 1f,
                    remainingEtaMinutes = 0,
                    remainingDistanceKm = 0f,
                    isTracking = false,
                    isDelivered = true,
                    activeTimelineStep = 3
                )
            }
        }
    }

    override fun onCleared() {
        trackingJob?.cancel()
        super.onCleared()
    }

    companion object {
        const val INITIAL_ETA_MINUTES = 10
        const val TOTAL_DISTANCE_KM = 3.8f
        private const val SIMULATION_DURATION_MS = 45_000L
        private val SIMULATION_STEPS = 90
        private val SIMULATION_STEP_DELAY_MS = SIMULATION_DURATION_MS / SIMULATION_STEPS
    }
}
