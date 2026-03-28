package com.example.tamangfood.presentation.ui.authentication.forgotpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentResetPasswordSuccessBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordSuccessFragment : Fragment(R.layout.fragment_reset_password_success) {
    private var _binding: FragmentResetPasswordSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResetPasswordSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setReturnHomeButtonText()
        setOnClickListeners()
    }

    private fun setReturnHomeButtonText(){
        val navController = findNavController()

        when (navController.graph.id) {
            R.id.main_navigation -> {
                binding.btnReturnHome.text = "Return settings"
            }
            R.id.auth_navigation -> {
                binding.btnReturnHome.text = "Return sign in"
            }
        }
    }

    private fun setOnClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnReturnHome.setOnClickListener {
            val navController = findNavController()

            when (navController.graph.id) {
                R.id.auth_navigation -> {
                    navController.navigate(R.id.signInFragment)
                }

                R.id.main_navigation -> {
                    navController.navigate(R.id.settingsFragment)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
