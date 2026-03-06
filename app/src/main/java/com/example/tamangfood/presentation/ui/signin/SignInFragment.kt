package com.example.tamangfood.presentation.ui.signin

import android.os.Bundle
import android.text.InputType
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

        binding.icVisible.setOnClickListener {
            viewModel.togglePassword()
        }

        binding.tvCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.errorEmail.observe(viewLifecycleOwner) { error ->
            binding.etEmail.error = error
        }

        viewModel.errorPassword.observe(viewLifecycleOwner) { error ->
            binding.etPassword.error = error
        }

        viewModel.passwordVisible.observe(viewLifecycleOwner) { visible ->
            togglePassword(visible)
        }

        viewModel.loginSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Utils.showToast(requireContext(),"Login successfully")
            }
        }
    }

    private fun handleSignIn() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        val request = SignInRequest(email, password)

        viewModel.signIn(request)
    }

    private fun togglePassword(visible: Boolean) {
        if (visible) {
            binding.etPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.icVisible.setImageResource(R.drawable.ic_eye)
        } else {
            binding.etPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.icVisible.setImageResource(R.drawable.ic_eye_off)
        }

        binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
    }
}