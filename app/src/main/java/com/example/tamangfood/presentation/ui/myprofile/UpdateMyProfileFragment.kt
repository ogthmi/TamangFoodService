package com.example.tamangfood.presentation.ui.myprofile

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentUpdateMyProfileBinding
import java.util.Calendar

class UpdateMyProfileFragment : Fragment(R.layout.fragment_update_my_profile) {
    private var _binding: FragmentUpdateMyProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UpdateMyProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentUpdateMyProfileBinding.bind(view)

        setupClickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.etDob.setOnClickListener {
            showDatePicker()
        }

        binding.btnSignUp.setOnClickListener {
            handleSignUp()
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

    private fun handleSignUp() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phoneNumber = binding.etPhone.text.toString().trim()
        val dateOfBirth = binding.etDob.text.toString().trim()

        if (fullName.isBlank()) {
            binding.layoutFullName.helperText = "Full name is required"
            binding.etFullName.setBackgroundResource(R.drawable.edittext_error)
        }

        if (email.isEmpty()) {
            binding.layoutEmail.helperText = "Email is required"
            binding.etEmail.setBackgroundResource(R.drawable.edittext_error)
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.layoutEmail.helperText = "Invalid email"
            binding.etEmail.setBackgroundResource(R.drawable.edittext_error)
        }

        if (phoneNumber.isEmpty()) {
            binding.layoutPhone.helperText = "Phone number is required"
            binding.etPhone.setBackgroundResource(R.drawable.edittext_error)
        }

        if (phoneNumber.length < 10) {
            binding.layoutPhone.helperText = "Invalid phone number"
            binding.etPhone.setBackgroundResource(R.drawable.edittext_error)
        }

        if (dateOfBirth.isBlank()) {
            binding.layoutDob.helperText = "Date of birth is required"
            binding.etDob.setBackgroundResource(R.drawable.edittext_error)
        }

        //TO DO: call view model
    }
}