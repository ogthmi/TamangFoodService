package com.example.tamangfood.presentation.ui.mainapp.home.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.event.FavoriteEventBus
import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.domain.usecase.AddFoodToFavoriteUseCase
import com.example.tamangfood.domain.usecase.DeleteFoodFromFavoriteUseCase
import com.example.tamangfood.domain.usecase.GetRecommendedFoodsUseCase
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
class RecommendViewModel @Inject constructor(
    private val getRecommendedFoodsUseCase: GetRecommendedFoodsUseCase,
    private val addFoodToFavoriteUseCase: AddFoodToFavoriteUseCase,
    private val deleteFoodFromFavoriteUseCase: DeleteFoodFromFavoriteUseCase,
    private val favoriteEventBus: FavoriteEventBus
) : ViewModel() {

    private val _uiState = MutableStateFlow<NetworkState>(NetworkState.Loading)
    val uiState: StateFlow<NetworkState> = _uiState.asStateFlow()

    private val _favoriteState = MutableSharedFlow<String>()
    val favoriteState: SharedFlow<String> = _favoriteState.asSharedFlow()

    init {
        loadRecommendedFoods()
        observeFavoriteEvents()
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

    fun toggleFavorite(food: Food) {
        viewModelScope.launch {
            val isLiked = food.hasLiked
            try {
                val flow = if (isLiked) {
                    deleteFoodFromFavoriteUseCase.execute(food.id)
                } else {
                    addFoodToFavoriteUseCase.execute(food.id)
                }
                flow.collect { state ->
                    when (state) {
                        is NetworkState.Success<*> -> {
                            updateFoodLike(food.id, !isLiked)
                            favoriteEventBus.emit(food.id, !isLiked)
                            _favoriteState.emit(
                                if (isLiked) "Deleted ${food.name} from favorite"
                                else "Added ${food.name} to favorite"
                            )
                        }
                        is NetworkState.Error -> {
                            _favoriteState.emit(state.message)
                        }
                        else -> Unit
                    }
                }
            } catch (e: Exception) {
                _favoriteState.emit(
                    if (isLiked) "Error deleting ${food.name} from favorite"
                    else "Error adding ${food.name} to favorite"
                )
            }
        }
    }

    private fun observeFavoriteEvents() {
        viewModelScope.launch {
            favoriteEventBus.events.collect { event ->
                updateFoodLike(event.foodId, event.hasLiked)
            }
        }
    }

    private fun updateFoodLike(foodId: Int, hasLiked: Boolean) {

        fun update(state: NetworkState): NetworkState {
            if (state is NetworkState.Success<*>) {

                val oldList = state.data as List<Food>

                val newList = oldList.map { food ->
                    if (food.id == foodId) {
                        food.copy(hasLiked = hasLiked)
                    } else {
                        food
                    }
                }.toList()

                return NetworkState.Success(newList)
            }
            return state
        }

        _uiState.value = update(_uiState.value)
    }
}