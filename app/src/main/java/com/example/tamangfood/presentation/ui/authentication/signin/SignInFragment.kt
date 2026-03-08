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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
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

        binding.ivFingerprint.setOnClickListener {
            setupFingerPrint()
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
        navigateToHome()
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

    private fun navigateToHome(){
        findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToMainAppFragment(),
            navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.signInFragment, true)
                .build()
        )
    }
}