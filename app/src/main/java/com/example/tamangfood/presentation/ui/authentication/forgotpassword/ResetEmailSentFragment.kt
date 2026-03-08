package com.example.tamangfood.presentation.ui.authentication.forgotpassword

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentEmailSentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetEmailSentFragment : Fragment(R.layout.fragment_email_sent) {
    private var _binding: FragmentEmailSentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentEmailSentBinding.bind(view)

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
    }

    private fun observeViewModel() {

    }

    private fun handleForgotPassword() {
    }
}