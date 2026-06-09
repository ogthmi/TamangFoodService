package com.example.tamangfood.presentation.ui.mainapp.home.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.tamangfood.data.paging.FilterFoodPagingSource
import com.example.tamangfood.domain.event.FavoriteEventBus
import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.domain.usecase.AddFoodToFavoriteUseCase
import com.example.tamangfood.domain.usecase.DeleteFoodFromFavoriteUseCase
import com.example.tamangfood.domain.usecase.GetCategoriesUseCase
import com.example.tamangfood.domain.usecase.GetCategoryDetailsUseCase
import com.example.tamangfood.domain.usecase.GetFilteredFoodsUseCase
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

data class FilterParams(
    val categoryDetailIds: List<Long>,
    val rating: Int
)

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getCategoryDetailsUseCase: GetCategoryDetailsUseCase,
    private val getFilteredFoodsUseCase: GetFilteredFoodsUseCase,
    private val addFoodToFavoriteUseCase: AddFoodToFavoriteUseCase,
    private val deleteFoodFromFavoriteUseCase: DeleteFoodFromFavoriteUseCase,
    private val favoriteEventBus: FavoriteEventBus
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val categoriesState: StateFlow<NetworkState> = _categoriesState.asStateFlow()

    private val _detailsState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val detailsState: StateFlow<NetworkState> = _detailsState.asStateFlow()

    private val _favoriteState = MutableSharedFlow<String>()
    val favoriteState: SharedFlow<String> = _favoriteState.asSharedFlow()

    private val _favoriteUpdates = MutableStateFlow<Map<Int, Boolean>>(emptyMap())

    private val _filterParams = MutableStateFlow<FilterParams?>(null)

    private val filterPaging: Flow<PagingData<Food>> = _filterParams
        .flatMapLatest { params ->
            if (params == null) {
                flowOf(PagingData.empty())
            } else {
                Pager(
                    config = PagingConfig(
                        pageSize = 20,
                        prefetchDistance = 5,
                        enablePlaceholders = false,
                        initialLoadSize = 20
                    ),
                    pagingSourceFactory = {
                        FilterFoodPagingSource(
                            getFilteredFoodsUseCase = getFilteredFoodsUseCase,
                            categoryDetailIds = params.categoryDetailIds,
                            rating = params.rating
                        )
                    }
                ).flow
            }
        }
        .cachedIn(viewModelScope)

    val filterFoods: Flow<PagingData<Food>> =
        combine(filterPaging, _favoriteUpdates) { paging, favMap ->
            paging.map { food ->
                val updated = favMap[food.id] ?: food.hasLiked
                food.copy(hasLiked = updated)
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

    fun loadCategoryDetails(categoryId: Int) {
        viewModelScope.launch {
            getCategoryDetailsUseCase.execute(categoryId).collect { state ->
                _detailsState.value = state
            }
        }
    }

    fun resetDetailsState() {
        _detailsState.value = NetworkState.Init
    }

    fun applyFilter(categoryDetailIds: List<Long>, rating: Int) {
        _favoriteUpdates.value = emptyMap()
        _filterParams.value = FilterParams(categoryDetailIds, rating)
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
                            _favoriteUpdates.value += (food.id to !isLiked)
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
                _favoriteUpdates.value += (event.foodId to event.hasLiked)
            }
        }
    }
}
