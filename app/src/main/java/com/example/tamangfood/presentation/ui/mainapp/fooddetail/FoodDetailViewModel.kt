package com.example.tamangfood.presentation.ui.mainapp.fooddetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.model.FoodComment
import com.example.tamangfood.domain.model.FoodDetail
import com.example.tamangfood.domain.usecase.AddCartItemUseCase
import com.example.tamangfood.domain.usecase.GetFoodCommentsUseCase
import com.example.tamangfood.domain.usecase.GetFoodDetailUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FoodDetailUiState {
    data object Loading : FoodDetailUiState()
    data class Loaded(val detail: FoodDetail) : FoodDetailUiState()
    data class Failed(val message: String) : FoodDetailUiState()
}

@HiltViewModel
class FoodDetailViewModel @Inject constructor(
    private val getFoodDetailUseCase: GetFoodDetailUseCase,
    private val getFoodCommentsUseCase: GetFoodCommentsUseCase,
    private val addCartItemUseCase: AddCartItemUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val foodId: Int = checkNotNull(savedStateHandle["foodId"]) {
        "foodId argument is required"
    }

    private val _uiState = MutableStateFlow<FoodDetailUiState>(FoodDetailUiState.Loading)
    val uiState: StateFlow<FoodDetailUiState> = _uiState.asStateFlow()

    private val _comments = MutableStateFlow<List<FoodComment>>(emptyList())
    val comments: StateFlow<List<FoodComment>> = _comments.asStateFlow()

    private val _addToCartState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val addToCartState: StateFlow<NetworkState> = _addToCartState.asStateFlow()

    init {
        load()
        loadComments()
    }

    fun load() {
        viewModelScope.launch {
            getFoodDetailUseCase.execute(foodId).collect { state ->
                when (state) {
                    is NetworkState.Loading -> _uiState.value = FoodDetailUiState.Loading
                    is NetworkState.Success<*> -> {
                        @Suppress("UNCHECKED_CAST")
                        val detail = state.data as? FoodDetail
                        _uiState.value = if (detail != null) {
                            FoodDetailUiState.Loaded(detail)
                        } else {
                            FoodDetailUiState.Failed("")
                        }
                    }
                    is NetworkState.Error ->
                        _uiState.value = FoodDetailUiState.Failed(state.message)
                    else -> Unit
                }
            }
        }
    }

    fun loadComments() {
        viewModelScope.launch {
            try {
                _comments.value = getFoodCommentsUseCase.execute(foodId)
            } catch (_: Exception) {
                _comments.value = emptyList()
            }
        }
    }

    fun reloadDetailAndComments() {
        load()
        loadComments()
    }

    fun addToCart(quantity: Int, ingredientIds: List<Int> = emptyList()) {
        if (quantity <= 0) {
            _addToCartState.value = NetworkState.Error("Invalid quantity")
            return
        }
        viewModelScope.launch {
            addCartItemUseCase.execute(
                foodId = foodId,
                quantity = quantity,
                ingredientIds = ingredientIds
            ).collect { state ->
                _addToCartState.value = state
            }
        }
    }

    fun clearAddToCartState() {
        _addToCartState.value = NetworkState.Init
    }
}
