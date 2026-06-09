package com.example.tamangfood.presentation.ui.mainapp.order.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.usecase.LeaveCommentUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaveReviewViewModel @Inject constructor(
    private val leaveCommentUseCase: LeaveCommentUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _submitResult = MutableSharedFlow<NetworkState>()
    val submitResult: SharedFlow<NetworkState> = _submitResult.asSharedFlow()

    fun submitReview(orderId: Int, foodId: Int, rating: Double, comment: String) {
        viewModelScope.launch {
            leaveCommentUseCase.execute(orderId, foodId, rating, comment).collect { state ->
                when (state) {
                    is NetworkState.Loading -> _isLoading.value = true
                    else -> {
                        _isLoading.value = false
                        _submitResult.emit(state)
                    }
                }
            }
        }
    }
}
