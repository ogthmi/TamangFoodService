package com.example.tamangfood.presentation.ui.authentication.signin

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentSignInBinding
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SignInViewModel by viewModels()
    private val enrollLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
        }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeSignInState()
        val inputs = listOf(
            binding.etEmail,
            binding.etPassword
        )

        inputs.forEach {editText ->
            editText.addTextChangedListener {
                handleSignIn(false)
            }
        }
    }

    private fun observeSignInState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signInState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> {
                            binding.btnSignIn.isEnabled = false
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is NetworkState.Success<*> -> {
                            binding.progressBar.visibility = View.GONE
                            navigateToHome()
                        }
                        is NetworkState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.btnSignIn.isEnabled = true
                            Utils.showToast(requireContext(), state.message)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupClickListeners() {
        binding.btnSignIn.setOnClickListener {
            if(handleSignIn(true)) {
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()
                viewModel.signIn(email, password)
            }
        }

        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.forgotPasswordFragment)
        }

        binding.tvCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.signUpFragment)
        }

        binding.ivFingerprint.setOnClickListener {
            setupFingerPrint()
        }

    }

    private fun handleSignIn(isSubmit: Boolean): Boolean {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        var isValid = true
        clearValidationErrors()

        if (email.isEmpty() && isSubmit) {
            binding.layoutEmail.helperText = "Email is required"
            binding.etEmail.setBackgroundResource(R.drawable.edittext_error)
            isValid = false
        }

        if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.layoutEmail.helperText = "Invalid email"
            binding.etEmail.setBackgroundResource(R.drawable.edittext_error)
            isValid = false
        }

        if (password.isEmpty() && isSubmit) {
            binding.layoutPassword.helperText = "Password is required"
            binding.etPassword.setBackgroundResource(R.drawable.edittext_error)
            isValid = false
        }

        return isValid
    }

    private fun clearValidationErrors() {
        binding.layoutEmail.helperText = null
        binding.layoutPassword.helperText = null
        binding.etEmail.setBackgroundResource(R.drawable.edittext_underline)
        binding.etPassword.setBackgroundResource(R.drawable.edittext_underline)
    }

    private fun setupFingerPrint(){
        val biometricManager = BiometricManager.from(requireContext())

        when (biometricManager.canAuthenticate(
            BIOMETRIC_STRONG
        )) {

            BiometricManager.BIOMETRIC_SUCCESS -> {
                showBiometricPrompt()
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Utils.showToast(requireContext(), "No biometric features available on this device.")
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Utils.showToast(requireContext(), "Biometric features are currently unavailable.")
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                openBiometricEnroll()
            }
        }
    }

    private fun showBiometricPrompt() {

        val executor = ContextCompat.getMainExecutor(requireContext())

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    Utils.showToast(requireContext(), "Authentication succeeded!")
                    navigateToHome()
                }

                override fun onAuthenticationFailed() {
                    Utils.showToast(requireContext(), "Authentication failed!")
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Sử dụng vân tay")
            .setSubtitle("Xác thực để tiếp tục")
            .setAllowedAuthenticators(
                BIOMETRIC_STRONG
            )
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun openBiometricEnroll() {
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BIOMETRIC_STRONG
            )
        }
        enrollLauncher.launch(enrollIntent)
    }

    private fun navigateToHome() {
        findNavController().navigate(
            R.id.action_signInFragment_to_mainAppFragment,
            null,
            NavOptions.Builder()
                .setPopUpTo(R.id.signInFragment, true)
                .build()
        )
    }
}