package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.updateprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamangfood.domain.usecase.GetUserProfileUserCase
import com.example.tamangfood.domain.usecase.UpdateMyProfileUseCase
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File

@HiltViewModel
class UpdateMyProfileViewModel @Inject constructor(
    private val updateMyProfileUseCase: UpdateMyProfileUseCase,
    private val getUserProfileUseCase: GetUserProfileUserCase
) : ViewModel() {
    private var _updateMyProfileState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val updateMyProfileState: MutableStateFlow<NetworkState> = _updateMyProfileState

    private val _userProfileState = MutableStateFlow<NetworkState>(NetworkState.Init)
    val userProfileState: MutableStateFlow<NetworkState> = _userProfileState

    fun getUserProfile(userId: Int) {
        _userProfileState.value = NetworkState.Loading

        viewModelScope.launch {
            getUserProfileUseCase.execute(userId)
                .collect { state -> _userProfileState.value = state }
        }
    }

    fun updateMyProfile(
        fullName: String, phoneNumber: String, dateOfBirth: Long, imageFile: File?
    ) {
        _updateMyProfileState.value = NetworkState.Loading

        viewModelScope.launch {
            updateMyProfileUseCase.execute(fullName, phoneNumber, dateOfBirth, imageFile)
                .collect { state -> _updateMyProfileState.value = state }
        }
    }
}