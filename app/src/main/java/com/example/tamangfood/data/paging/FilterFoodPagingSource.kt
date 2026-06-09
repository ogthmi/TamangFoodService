package com.example.tamangfood.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.domain.usecase.GetFilteredFoodsUseCase

class FilterFoodPagingSource(
    private val getFilteredFoodsUseCase: GetFilteredFoodsUseCase,
    private val categoryDetailIds: List<Long>,
    private val rating: Int
) : PagingSource<Int, Food>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Food> {
        return try {
            val page = params.key ?: 0
            val result = getFilteredFoodsUseCase.execute(
                categoryDetailIds = categoryDetailIds,
                rating = rating,
                page = page,
                size = params.loadSize
            )
            LoadResult.Page(
                data = result.items,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (result.isLastPage) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Food>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
        }
    }
}
