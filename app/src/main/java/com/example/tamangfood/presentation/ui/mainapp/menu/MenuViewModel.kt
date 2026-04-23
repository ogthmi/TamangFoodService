package com.example.tamangfood.presentation.ui.mainapp.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.example.tamangfood.domain.event.FavoriteEventBus
import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.data.paging.FoodPagingSource
import com.example.tamangfood.domain.model.FoodCategory
import com.example.tamangfood.domain.usecase.AddFoodToFavoriteUseCase
import com.example.tamangfood.domain.usecase.DeleteFoodFromFavoriteUseCase
import com.example.tamangfood.domain.usecase.GetCategoriesUseCase
import com.example.tamangfood.domain.usecase.GetFoodsByCategoryUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getFoodsByCategoryUseCase: GetFoodsByCategoryUseCase,
    private val addFoodToFavoriteUseCase: AddFoodToFavoriteUseCase,
    private val deleteFoodFromFavoriteUseCase: DeleteFoodFromFavoriteUseCase,
    private val favoriteEventBus: FavoriteEventBus
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val categoriesState: StateFlow<NetworkState> = _categoriesState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<FoodCategory?>(null)

    private val _menuSearchQuery = MutableStateFlow("")
    val menuSearchQuery: StateFlow<String> = _menuSearchQuery.asStateFlow()

    private val _favoriteState = MutableSharedFlow<String>()
    val favoriteState: SharedFlow<String> = _favoriteState.asSharedFlow()

    private val _favoriteUpdates = MutableStateFlow<Map<Int, Boolean>>(emptyMap())

    private var pagingSource: FoodPagingSource? = null

    private val categoryMenuFoodsPaging: Flow<PagingData<Food>> = _selectedCategory
        .flatMapLatest { category ->
            if (category == null) {
                flowOf(PagingData.empty())
            } else {
                Pager(
                    config = PagingConfig(
                        pageSize = PAGE_SIZE,
                        prefetchDistance = 5,
                        enablePlaceholders = false,
                        initialLoadSize = PAGE_SIZE
                    ),
                    pagingSourceFactory = {
                        FoodPagingSource(getFoodsByCategoryUseCase, category.id)
                            .also { pagingSource = it }
                    }
                ).flow
            }
        }
        .cachedIn(viewModelScope)

    val menuFoodsPaging: Flow<PagingData<Food>> =
        combine(
            categoryMenuFoodsPaging,
            _favoriteUpdates
        ) { paging, favMap ->
            paging.map { food ->
                val updatedLike = favMap[food.id] ?: food.hasLiked
                food.copy(hasLiked = updatedLike)
            }
        }.cachedIn(viewModelScope)

    init {
        loadCategories()
        observeFavoriteEvents()
    }

    fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase.execute().collect { state ->
                _categoriesState.value = state
            }
        }
    }

    fun selectCategory(category: FoodCategory) {
        _menuSearchQuery.value = ""
        _selectedCategory.value = category
    }

    fun clearMenuFoods() {
        _menuSearchQuery.value = ""
        _selectedCategory.value = null
    }

    fun setMenuSearchQuery(query: String) {
        _menuSearchQuery.value = query
    }

    companion object {
        const val PAGE_SIZE = 20
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
                            _favoriteUpdates.value += (food.id to newState)
                            favoriteEventBus.emit(food.id, newState)
                            _favoriteState.emit(
                                if (newState) "Added ${food.name} to favorite"
                                else "Deleted ${food.name} from favorite"
                            )
                        }
                        is NetworkState.Error -> {
                            _favoriteState.emit(state.message)
                        }
                        else -> Unit
                    }
                }
            } catch (e: Exception) {
                _favoriteState.emit("Error updating ${food.name}")
            }
        }
    }

    private fun observeFavoriteEvents() {
        viewModelScope.launch {
            favoriteEventBus.events.collect { event ->
                _favoriteUpdates.value += (event.foodId to event.hasLiked)
            }
        }
    }

}
