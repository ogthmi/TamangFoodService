package com.example.tamangfood.presentation.ui.authentication.forgotpassword

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentEmailSentBinding
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetEmailSentFragment : Fragment(R.layout.fragment_email_sent) {

    private var _binding: FragmentEmailSentBinding? = null
    private val binding get() = _binding!!

    private val forgotPasswordViewModel: ForgotPasswordViewModel by activityViewModels()
    private val resetEmailSentViewModel: ResetEmailSentViewModel by viewModels()

    private var countDownTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmailSentBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCountdown()
        setClickListeners()
        observeForgotPasswordViewModel()
        observeResetEmailSentViewModel()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }

    private fun setClickListeners() {
        val email = forgotPasswordViewModel.email

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnResendEmail.setOnClickListener {
            if (email != null) {
                forgotPasswordViewModel.forgotPassword(email)
                startCountdown()
            } else {
                Utils.showToast(requireContext(), "Email not found")
            }
        }

        binding.btnResetPassword.setOnClickListener {
            val otp = binding.etOtpCode.text?.toString() ?: ""
            val newPassword = binding.etNewPassword.text?.toString() ?: ""
            val confirmPassword = binding.etConfirmPassword.text?.toString() ?: ""

            if (email != null && validateInputs(otp, newPassword, confirmPassword)) {
                resetEmailSentViewModel.resetPassword(otp, email, newPassword, confirmPassword)
            }

            binding.etOtpCode.clearErrorOnTyping(binding.otpCodeLayout)
            binding.etNewPassword.clearErrorOnTyping(binding.newPasswordLayout)
            binding.etConfirmPassword.clearErrorOnTyping(binding.confirmPasswordLayout)
        }
    }

    private fun observeForgotPasswordViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            forgotPasswordViewModel.forgotPasswordState.collect { state ->
                when (state) {
                    is NetworkState.Init -> Unit

                    is NetworkState.Loading -> {
                        binding.btnResendEmail.isEnabled = false
                    }

                    is NetworkState.Success<*> -> {
                        Utils.showToast(requireContext(), "Email sent again")
                        forgotPasswordViewModel.resetState()
                    }

                    is NetworkState.Error -> {
                        countDownTimer?.cancel()
                        binding.btnResendEmail.apply {
                            isEnabled = true
                            text = "Send again"
                            background = ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.button_orange
                            )
                        }
                        Utils.showToast(requireContext(), state.message)
                        forgotPasswordViewModel.resetState()
                    }
                }
            }
        }
    }

    private fun observeResetEmailSentViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            resetEmailSentViewModel.resetPasswordState.collect { state ->
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

                        val action = ResetEmailSentFragmentDirections
                            .actionResetEmailSentFramentToResetEmailSuccessFragment()
                        findNavController().navigate(action)
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

                        resetEmailSentViewModel.resetState()
                    }
                }
            }
        }
    }

    private fun validateInputs(otp: String, newPassword: String, confirmPassword: String): Boolean {
        if (otp.isEmpty()) {
            binding.otpCodeLayout.helperText = "OTP code is required"
            binding.etOtpCode.setBackgroundResource(R.drawable.edittext_error)
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

    private fun AppCompatEditText.clearErrorOnTyping(textInputLayout: TextInputLayout) {
        this.addTextChangedListener {
            textInputLayout.helperText = null
            this.setBackgroundResource(R.drawable.edittext_underline)
        }
    }

    private fun startCountdown() {
        binding.btnResendEmail.isEnabled = false

        countDownTimer?.cancel()

        countDownTimer = object : CountDownTimer(30000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding.btnResendEmail.apply {
                    text = "Resend (${seconds + 1} s)"
                    background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.button_disable
                    )
                }
            }

            override fun onFinish() {
                binding.btnResendEmail.apply {
                    isEnabled = true
                    text = "Resend"
                    background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.button_orange
                    )
                }
            }

        }.start()
    }
}