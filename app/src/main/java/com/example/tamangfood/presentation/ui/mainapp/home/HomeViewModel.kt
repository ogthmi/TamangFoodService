package com.example.tamangfood.presentation.ui.mainapp.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.usecase.GetBestSellerFoodsUseCase
import com.example.tamangfood.domain.usecase.GetCategoriesUseCase
import com.example.tamangfood.domain.usecase.GetRecommendedFoodsUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getRecommendedFoodsUseCase: GetRecommendedFoodsUseCase,
    private val getBestSellerFoodsUseCase: GetBestSellerFoodsUseCase
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val categoriesState: StateFlow<NetworkState> = _categoriesState.asStateFlow()

    private val _recommendState = MutableStateFlow<NetworkState>(NetworkState.Loading)
    val recommendState: StateFlow<NetworkState> = _recommendState.asStateFlow()

    private val _bestSellerState = MutableStateFlow<NetworkState>(NetworkState.Loading)
    val bestSellerState: StateFlow<NetworkState> = _bestSellerState.asStateFlow()

    init {
        loadCategories()
        loadRecommendedFoods()
        loadBestSellerFoods()
    }

    fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase.execute().collect { state ->
                _categoriesState.value = state
            }
        }
    }

    fun loadRecommendedFoods() {
        viewModelScope.launch {
            _recommendState.value = NetworkState.Loading
            try {
                val list = getRecommendedFoodsUseCase.execute()
                _recommendState.value = NetworkState.Success(list)
            } catch (e: Exception) {
                _recommendState.value = NetworkState.Error(e.message ?: "Error")
            }
        }
    }

    fun loadBestSellerFoods() {
        viewModelScope.launch {
            _bestSellerState.value = NetworkState.Loading
            try {
                val list = getBestSellerFoodsUseCase.execute()
                _bestSellerState.value = NetworkState.Success(list)
            } catch (e: Exception) {
                _bestSellerState.value = NetworkState.Error(e.message ?: "Error")
            }
        }
    }
}
