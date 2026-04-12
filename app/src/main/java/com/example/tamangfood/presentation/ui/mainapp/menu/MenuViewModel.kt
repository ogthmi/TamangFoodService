package com.example.tamangfood.presentation.ui.mainapp.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.data.paging.FoodPagingSource
import com.example.tamangfood.domain.model.FoodCategory
import com.example.tamangfood.domain.usecase.GetCategoriesUseCase
import com.example.tamangfood.domain.usecase.GetFoodsByCategoryUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getFoodsByCategoryUseCase: GetFoodsByCategoryUseCase
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val categoriesState: StateFlow<NetworkState> = _categoriesState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<FoodCategory?>(null)

    private val _menuSearchQuery = MutableStateFlow("")
    val menuSearchQuery: StateFlow<String> = _menuSearchQuery.asStateFlow()

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
                        FoodPagingSource(
                            getFoodsByCategoryUseCase = getFoodsByCategoryUseCase,
                            categoryId = category.id
                        )
                    }
                ).flow
            }
        }
        .cachedIn(viewModelScope)

    val menuFoodsPaging: Flow<PagingData<Food>> = combine(
        categoryMenuFoodsPaging,
        _menuSearchQuery
    ) { pagingData, rawQuery ->
        val q = rawQuery.trim()
        if (q.isEmpty()) pagingData
        else pagingData.filter { food -> food.name.contains(q, ignoreCase = true) }
    }.cachedIn(viewModelScope)

    init {
        loadCategories()
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
}
