package com.example.tamangfood.presentation.ui.mainapp.home.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.usecase.GetRecommendedFoodsUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendViewModel @Inject constructor(
    private val getRecommendedFoodsUseCase: GetRecommendedFoodsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<NetworkState>(NetworkState.Loading)
    val uiState: StateFlow<NetworkState> = _uiState.asStateFlow()

    init {
        loadRecommendedFoods()
    }

    fun loadRecommendedFoods() {
        viewModelScope.launch {
            _uiState.value = NetworkState.Loading
            try {
                val list = getRecommendedFoodsUseCase.execute()
                _uiState.value = NetworkState.Success(list)
            } catch (e: Exception) {
                _uiState.value = NetworkState.Error(e.message ?: "Error")
            }
        }
    }
}