package com.example.tamangfood.domain.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

data class FavoriteChangedEvent(val foodId: Int, val hasLiked: Boolean)

@Singleton
class FavoriteEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<FavoriteChangedEvent>(extraBufferCapacity = 16)
    val events: SharedFlow<FavoriteChangedEvent> = _events.asSharedFlow()

    suspend fun emit(foodId: Int, hasLiked: Boolean) {
        _events.emit(FavoriteChangedEvent(foodId, hasLiked))
    }
}
