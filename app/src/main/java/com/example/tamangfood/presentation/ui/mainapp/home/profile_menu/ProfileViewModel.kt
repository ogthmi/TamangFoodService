package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.usecase.GetUserProfileUserCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUserCase: GetUserProfileUserCase
) : ViewModel() {
    private var _profileState = MutableStateFlow<NetworkState>(NetworkState.Init);
    val profileState: MutableStateFlow<NetworkState> = _profileState;

    fun getUserProfile(userId: Int){
        viewModelScope.launch {
            _profileState.value = NetworkState.Loading

            viewModelScope.launch {
                getUserProfileUserCase.execute(userId)
                    .collect { state -> _profileState.value = state }
            }
        }
    }
}