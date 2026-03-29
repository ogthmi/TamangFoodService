package com.example.tamangfood.presentation.ui.authentication.signup

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentSignUpBinding
import com.example.tamangfood.data.model.auth.signup.SignUpRequest
import com.example.tamangfood.presentation.ui.authentication.signin.SignInFragmentDirections
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSignUpBinding.bind(view)

        setupClickListeners()
        observeViewModel()
        val inputs = listOf(
            binding.etEmail,
            binding.etPassword,
            binding.etDob,
            binding.etFullName,
            binding.etPhone
        )

        inputs.forEach {editText ->
            editText.addTextChangedListener {
                handleSignUp(false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvSignIn.setOnClickListener {
            findNavController().navigate(
                R.id.action_signUpFragment_to_signInFragment
            )
        }

        binding.etDob.setOnClickListener {
            showDatePicker()
        }

        binding.btnSignUp.setOnClickListener {
            if(handleSignUp(true)){
                val fullName = binding.etFullName.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()
                val phoneNumber = binding.etPhone.text.toString().trim()
                val dateOfBirth = binding.etDob.text.toString().trim()
                signUp(email, password, fullName, phoneNumber, dateOfBirth)
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val date = "$day/${month + 1}/$year"
                binding.etDob.setText(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePicker.show()
    }

    private fun handleSignUp(isSubmit: Boolean): Boolean {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val phoneNumber = binding.etPhone.text.toString().trim()
        val dateOfBirth = binding.etDob.text.toString().trim()
        var isValid = true
        clearValidationErrors()

        if (fullName.isBlank() && isSubmit) {
            binding.layoutFullName.helperText = "Full name is required"
            binding.etFullName.setBackgroundResource(R.drawable.edittext_error)
            isValid = false
        }

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

        if (password.isNotEmpty() && password.length < 6) {
            binding.layoutPassword.helperText = "Invalid password. Min length is 6."
            binding.etPassword.setBackgroundResource(R.drawable.edittext_error)
            isValid = false
        }

        if (phoneNumber.isEmpty() && isSubmit) {
            binding.layoutPhone.helperText = "Phone number is required"
            binding.etPhone.setBackgroundResource(R.drawable.edittext_error)
            isValid = false
        }

        if (phoneNumber.isNotEmpty() && phoneNumber.length < 10) {
            binding.layoutPhone.helperText = "Invalid phone number"
            binding.etPhone.setBackgroundResource(R.drawable.edittext_error)
            isValid = false
        }

        if (dateOfBirth.isBlank() && isSubmit) {
            binding.layoutDob.helperText = "Date of birth is required"
            binding.etDob.setBackgroundResource(R.drawable.edittext_error)
            isValid = false
        }

        return isValid
    }

    private fun clearValidationErrors() {
        binding.layoutEmail.helperText = null
        binding.layoutPassword.helperText = null
        binding.layoutPhone.helperText = null
        binding.layoutDob.helperText = null
        binding.layoutFullName.helperText = null
        binding.etEmail.setBackgroundResource(R.drawable.edittext_underline)
        binding.etPassword.setBackgroundResource(R.drawable.edittext_underline)
        binding.etDob.setBackgroundResource(R.drawable.edittext_underline)
        binding.etFullName.setBackgroundResource(R.drawable.edittext_underline)
        binding.etPhone.setBackgroundResource(R.drawable.edittext_underline)
    }

    private fun signUp(email: String, password: String, fullName: String, phoneNumber: String, dateOfBirth: String) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")

        val timestamp = sdf.parse(dateOfBirth)?.time ?: return
        viewModel.signUp(
            email = email,
            password = password,
            fullName = fullName,
            phoneNumber = phoneNumber,
            dateOfBirth = timestamp
        )
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.signUpState.collect { state ->
                when (state) {
                    is NetworkState.Init -> Unit
                    is NetworkState.Loading -> {
                        binding.btnSignUp.isEnabled = false
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is NetworkState.Success<*> -> {
                        binding.btnSignUp.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                        navigateToHome()
                    }
                    is NetworkState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnSignUp.isEnabled = true
                        Utils.showToast(requireContext(), state.message)
                    }
                }
            }
        }
    }

    private fun navigateToBiometric(){
        findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToFingerPrintFragment(),
            navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.signUpFragment, true)
                .setPopUpTo(R.id.signInFragment, true)
                .build())
    }

    private fun navigateToHome(){
        findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToMainAppFragment(),
            navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.signUpFragment, true)
                .setPopUpTo(R.id.signInFragment, true)
                .build())
    }

    private fun androidx.appcompat.widget.AppCompatEditText.clearErrorOnTyping(
        textInputLayout: com.google.android.material.textfield.TextInputLayout
    ) {
        this.addTextChangedListener {
            textInputLayout.helperText = null
            this.setBackgroundResource(R.drawable.edittext_underline)
        }
    }
}