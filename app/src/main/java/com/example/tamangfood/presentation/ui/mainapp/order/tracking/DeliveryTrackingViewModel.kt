package com.example.tamangfood.presentation.ui.mainapp.order.tracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.data.api.ApiService
import com.example.tamangfood.presentation.ui.mainapp.order.tracking.DeliveryTrackingViewModel.Companion.TOTAL_DISTANCE_KM
import com.example.tamangfood.presentation.utils.NetworkState
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

data class DeliveryTrackingOrderInfo(
    val orderId: Int,
    val shippingAddress: String,
    val latitude: Double,
    val longitude: Double
)

@HiltViewModel
class DeliveryTrackingViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeliveryTrackingUiState())
    val uiState: StateFlow<DeliveryTrackingUiState> = _uiState.asStateFlow()
    private val _orderInfoState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val orderInfoState: StateFlow<NetworkState> = _orderInfoState.asStateFlow()

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

    fun loadOrderById(orderId: Int) {
        if (_orderInfoState.value is NetworkState.Loading) return
        viewModelScope.launch {
            _orderInfoState.value = NetworkState.Loading
            runCatching { apiService.getOrderById(orderId) }
                .onSuccess { response ->
                    if (!response.isSuccessful) {
                        _orderInfoState.value = NetworkState.Error("Khong the tai thong tin don hang")
                        return@onSuccess
                    }
                    val result = response.body()
                        ?.getAsJsonObject("result")
                    if (result == null) {
                        _orderInfoState.value = NetworkState.Error("Khong tim thay don hang")
                        return@onSuccess
                    }

                    val latitude = result.get("latitude")?.asDouble ?: 0.0
                    val longitude = result.get("longitude")?.asDouble ?: 0.0
                    if (latitude == 0.0 && longitude == 0.0) {
                        _orderInfoState.value = NetworkState.Error("Don hang khong co toa do giao hang")
                        return@onSuccess
                    }

                    _orderInfoState.value = NetworkState.Success(
                        DeliveryTrackingOrderInfo(
                            orderId = result.get("id")?.asInt ?: orderId,
                            shippingAddress = result.get("address")?.asString.orEmpty(),
                            latitude = latitude,
                            longitude = longitude
                        )
                    )
                }
                .onFailure { throwable ->
                    _orderInfoState.value =
                        NetworkState.Error(throwable.message ?: "Khong the tai thong tin don hang")
                }
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
