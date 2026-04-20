package com.example.tamangfood.presentation.ui.mainapp.home.cart.addtocart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.usecase.AddCartItemUseCase
import com.example.tamangfood.domain.usecase.GetFoodDetailUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddToCartBottomSheetViewModel @Inject constructor(
    private val addCartItemUseCase: AddCartItemUseCase,
    private val getFoodDetailUseCase: GetFoodDetailUseCase
) : ViewModel() {

    private val _addToCartState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val addToCartState = _addToCartState.asStateFlow()
    private val _foodDetailState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val foodDetailState = _foodDetailState.asStateFlow()

    fun addToCart(foodId: Int, quantity: Int, ingredientIds: List<Int> = emptyList()) {
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

    fun resetAddToCartState() {
        _addToCartState.value = NetworkState.Init
    }

    fun loadFoodDetail(foodId: Int) {
        viewModelScope.launch {
            getFoodDetailUseCase.execute(foodId).collect { state ->
                _foodDetailState.value = state
            }
        }
    }

    fun resetFoodDetailState() {
        _foodDetailState.value = NetworkState.Init
    }
}
