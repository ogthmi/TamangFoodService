package com.example.tamangfood.presentation.ui.authentication.forgotpassword

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentForgotPasswordBinding
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {
    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ForgotPasswordViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            val email = binding.etEmail.text.toString().trim()

            val hasValidEmail = handleForgotPassword()
            if (hasValidEmail) viewModel.forgotPassword(email = email)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.forgotPasswordState.collect { state ->
                when (state) {
                    is NetworkState.Init -> Unit

                    is NetworkState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnResetPassword.apply {
                            isEnabled = false
                            background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.button_disable
                            )
                        }
                    }

                    is NetworkState.Success<*> -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnResetPassword.apply {
                            isEnabled = true
                            background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.button_orange
                            )
                        }
                        findNavController().navigate(R.id.action_forgotPasswordFragment_to_resetEmailSentFragment)
                        viewModel.resetState()
                    }

                    is NetworkState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnResetPassword.apply {
                            isEnabled = true
                            background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.button_orange
                            )
                        }
                        Utils.showToast(requireContext(), state.message)
                    }
                }
            }
        }
    }

    private fun handleForgotPassword(): Boolean {
        var email = binding.etEmail.text.toString().trim()

        if (email.isEmpty()) {
            binding.layoutEmail.helperText = "Email is required"
            binding.etEmail.setBackgroundResource(R.drawable.edittext_error)
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.layoutEmail.helperText = "Invalid email"
            binding.etEmail.setBackgroundResource(R.drawable.edittext_error)
            return false
        }

        return true
    }

}