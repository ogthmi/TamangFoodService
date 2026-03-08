package com.example.tamangfood.presentation.ui.forgotpassword

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment: Fragment(R.layout.fragment_forgot_password) {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentForgotPasswordBinding.bind(view)

        setClickListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setClickListeners() {
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.btnResetPassword.setOnClickListener {
            handleForgotPassword()
            findNavController().navigate(R.id.emailSentFragment)
        }
    }

    private fun observeViewModel() {

    }

    private fun handleForgotPassword() {
        var email = binding.etEmail.text.toString().trim()

        if (email.isEmpty()) {
            binding.layoutEmail.helperText = "Email is required"
            binding.etEmail.setBackgroundResource(R.drawable.edittext_error)
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.layoutEmail.helperText = "Invalid email"
            binding.etEmail.setBackgroundResource(R.drawable.edittext_error)
        }
    }

}