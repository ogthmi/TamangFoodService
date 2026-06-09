package com.example.tamangfood.presentation.ui.mainapp.help

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.usecase.SampleUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HelpViewModel @Inject constructor(
    private val sampleUseCase: SampleUseCase
) : ViewModel() {
    private val _sampleState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val sampleState: MutableStateFlow<NetworkState> = _sampleState

    fun sample(){
        _sampleState.value = NetworkState.Loading
        viewModelScope.launch {
            sampleUseCase.execute().collect{
                _sampleState.value = it
            }
        }
    }
}