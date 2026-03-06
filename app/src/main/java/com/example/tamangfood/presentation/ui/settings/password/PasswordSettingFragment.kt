package com.example.tamangfood.presentation.ui.settings.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentPasswordSettingBinding
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordSettingFragment : Fragment() {
    private var _binding: FragmentPasswordSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvForgotPassword.setOnClickListener {
            // TODO: Navigate to forgot password screen
        }

        binding.btnChangePassword.setOnClickListener {
            // TODO: Implement change password logic
            val currentPassword = binding.etCurrentPassword.text?.toString() ?: ""
            val newPassword = binding.etNewPassword.text?.toString() ?: ""
            val confirmPassword = binding.etConfirmPassword.text?.toString() ?: ""

            if (validateInputs(currentPassword, newPassword, confirmPassword)) {
                // TODO: Call API to change password
            }
        }
    }

    private fun validateInputs(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
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

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}