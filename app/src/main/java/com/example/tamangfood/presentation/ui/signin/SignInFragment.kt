package com.example.tamangfood.presentation.ui.signin

import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.R
import com.example.tamangfood.data.model.auth.SignInRequest
import com.example.tamangfood.databinding.FragmentSignInBinding
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignInViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSignInBinding.bind(view)

        setupClickListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupClickListeners() {
        binding.btnSignIn.setOnClickListener {
            handleSignIn()
        }

        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.forgotPasswordFragment)
        }

        binding.tvCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }

    }

    private fun observeViewModel() {

    }

    private fun handleSignIn() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty()){
            binding.layoutEmail.helperText = "Email is required"
            binding.etEmail.setBackgroundResource(R.drawable.edittext_error)
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.layoutEmail.helperText = "Invalid email"
            binding.etEmail.setBackgroundResource(R.drawable.edittext_error)
        }

        if (password.isEmpty()){
            binding.layoutPassword.helperText = "Password is required"
            binding.etPassword.setBackgroundResource(R.drawable.edittext_error)
        }

        val request = SignInRequest(email, password)

        viewModel.signIn(request)
    }

}