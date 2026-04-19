package com.example.tamangfood.presentation.ui.mainapp.home.bestseller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.usecase.GetBestSellerFoodsUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BestSellerViewModel @Inject constructor(
    private val getBestSellerFoodsUseCase: GetBestSellerFoodsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<NetworkState>(NetworkState.Loading)
    val uiState: StateFlow<NetworkState> = _uiState.asStateFlow()

    init {
        loadBestSellerFoods()
    }

    fun loadBestSellerFoods() {
        viewModelScope.launch {
            _uiState.value = NetworkState.Loading
            try {
                val list = getBestSellerFoodsUseCase.execute()
                _uiState.value = NetworkState.Success(list)
            } catch (e: Exception) {
                _uiState.value = NetworkState.Error(e.message ?: "Error")
            }
        }
    }
}
