package com.example.tamangfood.presentation.ui.mainapp.favorite

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.tamangfood.domain.event.FavoriteEventBus
import com.example.tamangfood.data.paging.FavoritePagingSource
import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.domain.usecase.AddFoodToFavoriteUseCase
import com.example.tamangfood.domain.usecase.DeleteFoodFromFavoriteUseCase
import com.example.tamangfood.domain.usecase.GetFavoriteFoodsUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getFavoriteFoodsUseCase: GetFavoriteFoodsUseCase,
    private val addFoodToFavoriteUseCase: AddFoodToFavoriteUseCase,
    private val deleteFoodFromFavoriteUseCase: DeleteFoodFromFavoriteUseCase,
    private val favoriteEventBus: FavoriteEventBus
) : ViewModel() {

    private val _favoriteState = MutableSharedFlow<String>()
    val favoriteState = _favoriteState.asSharedFlow()

    private val _favoriteUpdates = MutableStateFlow<Map<Int, Boolean>>(emptyMap())

    private val _needsRefresh = MutableStateFlow(false)
    val needsRefresh = _needsRefresh.asStateFlow()

    private val favoritePaging: Flow<PagingData<Food>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FavoritePagingSource(getFavoriteFoodsUseCase)
            }
        ).flow.cachedIn(viewModelScope)

    val favoriteFoods: Flow<PagingData<Food>> =
        combine(favoritePaging, _favoriteUpdates) { paging, updates ->
            paging.map { food ->
                val updated = updates[food.id] ?: food.hasLiked
                food.copy(hasLiked = updated)
            }
        }.cachedIn(viewModelScope)

    init {
        observeFavoriteEvents()
    }

    private fun observeFavoriteEvents() {
        viewModelScope.launch {
            favoriteEventBus.events.collect { _ ->
                _needsRefresh.value = true
            }
        }
    }

    fun consumeRefresh() {
        _favoriteUpdates.value = emptyMap()
        _needsRefresh.value = false
    }

    fun toggleFavorite(food: Food) {
        viewModelScope.launch {
            val newState = !food.hasLiked
            try {
                val flow = if (newState) {
                    addFoodToFavoriteUseCase.execute(food.id)
                } else {
                    deleteFoodFromFavoriteUseCase.execute(food.id)
                }
                flow.collect { state ->
                    when (state) {
                        is NetworkState.Success<*> -> {
                            favoriteEventBus.emit(food.id, newState)
                            _favoriteUpdates.value += (food.id to newState)
                        }
                        is NetworkState.Error -> {
                            _favoriteState.emit(state.message)
                        }
                        else -> Unit
                    }
                }
            } catch (e: Exception) {
                Log.e("FAV", "API FAIL", e)
            }
        }
    }
}