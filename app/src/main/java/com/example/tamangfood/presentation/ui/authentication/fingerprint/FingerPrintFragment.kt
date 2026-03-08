package com.example.tamangfood.presentation.ui.authentication.fingerprint

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentFingerPrintBinding
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FingerPrintFragment : Fragment() {
    private var _binding: FragmentFingerPrintBinding? = null
    private val binding get() = _binding!!
    private val enrollLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFingerPrintBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSetup.setOnClickListener {
            setupFingerPrint()
        }

        binding.btnSkip.setOnClickListener {
            navigateToHome()
        }
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
        findNavController().navigate(FingerPrintFragmentDirections.actionFingerPrintFragmentToMainAppFragment(),
            navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.signInFragment, true)
                .setPopUpTo(R.id.fingerPrintFragment, true)
                .build())
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}