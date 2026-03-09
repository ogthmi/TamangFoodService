package com.example.tamangfood.presentation.ui.mainapp.order.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaveReviewViewModel @Inject constructor() : ViewModel() {

    private val _reviewSubmitted = MutableLiveData<Boolean>()
    val reviewSubmitted: LiveData<Boolean> = _reviewSubmitted

    fun submitReview(orderId: Int, rating: Int, comment: String) {
        viewModelScope.launch {
            // In a real app, this would make an API call to submit the review
            // For now, we'll just simulate success
            _reviewSubmitted.value = true
        }
    }
}