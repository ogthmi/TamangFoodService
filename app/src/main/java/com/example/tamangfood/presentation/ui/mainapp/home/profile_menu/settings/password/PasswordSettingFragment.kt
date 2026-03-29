package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.settings.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentPasswordSettingBinding
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PasswordSettingFragment : Fragment() {
    private var _binding: FragmentPasswordSettingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PasswordSettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvForgotPassword.setOnClickListener {
            val action = PasswordSettingFragmentDirections
                .actionPasswordSettingFragmentToForgotPasswordFragment()
            findNavController().navigate(action)
        }

        binding.btnChangePassword.setOnClickListener {

            val currentPassword = binding.etCurrentPassword.text?.toString() ?: ""
            val newPassword = binding.etNewPassword.text?.toString() ?: ""
            val confirmPassword = binding.etConfirmPassword.text?.toString() ?: ""

            if (validateInputs(currentPassword, newPassword, confirmPassword)) {
                binding.btnChangePassword.isEnabled = false
                viewModel.changePassword(currentPassword, newPassword, confirmPassword)
            }
        }

        binding.etCurrentPassword.clearErrorOnTyping(binding.currentPasswordLayout)
        binding.etNewPassword.clearErrorOnTyping(binding.newPasswordLayout)
        binding.etConfirmPassword.clearErrorOnTyping(binding.confirmPasswordLayout)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.changePasswordState.collect { state ->
                when (state) {
                    is NetworkState.Init -> Unit
                    is NetworkState.Loading -> {
                        binding.btnChangePassword.isEnabled = false
                        binding.btnChangePassword.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.button_disable)
                    }

                    is NetworkState.Success<*> -> {
                        binding.btnChangePassword.isEnabled = true
                        binding.btnChangePassword.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.button_orange)

                        Utils.showToast(requireContext(), "Change password successfully")

                        binding.etCurrentPassword.text?.clear()
                        binding.etNewPassword.text?.clear()
                        binding.etConfirmPassword.text?.clear()
                        binding.currentPasswordLayout.helperText = null
                        binding.newPasswordLayout.helperText = null
                        binding.confirmPasswordLayout.helperText = null

                        viewModel.resetState()
                    }

                    is NetworkState.Error -> {
                        binding.btnChangePassword.isEnabled = true
                        binding.btnChangePassword.background =
                            ContextCompat.getDrawable(requireContext(), R.drawable.button_orange)

                        Utils.showToast(requireContext(), state.message)
                    }
                }
            }
        }
    }

    private fun validateInputs(
        currentPassword: String, newPassword: String, confirmPassword: String
    ): Boolean {
        if (currentPassword.isEmpty()) {
            binding.currentPasswordLayout.helperText = "Current password is required"
            binding.etCurrentPassword.setBackgroundResource(R.drawable.edittext_error)
            return false
        }
        if (newPassword.isEmpty()) {
            binding.newPasswordLayout.helperText = "New password is required"
            binding.etNewPassword.setBackgroundResource(R.drawable.edittext_error)
            return false
        }
        if (newPassword != confirmPassword) {
            binding.confirmPasswordLayout.helperText = "Passwords do not match"
            binding.etConfirmPassword.setBackgroundResource(R.drawable.edittext_error)
            return false
        }
        if (newPassword == currentPassword) {
            binding.newPasswordLayout.helperText = "New password is identical"
            binding.etNewPassword.setBackgroundResource(R.drawable.edittext_error)
            return false
        }
        return true
    }

    private fun androidx.appcompat.widget.AppCompatEditText.clearErrorOnTyping(
        textInputLayout: com.google.android.material.textfield.TextInputLayout
    ) {
        this.addTextChangedListener {
            textInputLayout.helperText = null
            this.setBackgroundResource(R.drawable.edittext_underline)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}