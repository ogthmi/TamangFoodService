package com.example.tamangfood.presentation.ui.signin

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tamangfood.R
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

        setupClick()
        observeViewModel()
    }

    private fun setupClick() {

        binding.btnSignIn.setOnClickListener {

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            viewModel.signIn(email, password)
        }

        binding.icVisible.setOnClickListener {
            viewModel.togglePassword()
        }

        binding.tvCreateAccount.setOnClickListener {
            // TODO: navigate sign up
        }

        binding.tvForgotPassword.setOnClickListener {
            // TODO: navigate forgot password
        }
    }

    private fun observeViewModel() {

        viewModel.errorEmail.observe(viewLifecycleOwner) {
            binding.etEmail.error = it
        }

        viewModel.errorPassword.observe(viewLifecycleOwner) {
            binding.etPassword.error = it
        }

        viewModel.passwordVisible.observe(viewLifecycleOwner) { visible ->

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

        viewModel.loginSuccess.observe(viewLifecycleOwner) {
            if (it) {
                Utils.showToast(requireContext(), "Login successfully")
                // TODO: navigate home
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}