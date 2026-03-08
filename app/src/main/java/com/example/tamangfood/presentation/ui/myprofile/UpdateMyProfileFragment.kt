package com.example.tamangfood.presentation.ui.myprofile

import android.app.Activity
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentUpdateMyProfileBinding
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.Calendar

class UpdateMyProfileFragment : Fragment(R.layout.fragment_update_my_profile) {

    private var _binding: FragmentUpdateMyProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UpdateMyProfileViewModel by viewModels()

    private var croppedImageUri: Uri? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { startCrop(it) }
        }

    private val cropLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val resultUri = UCrop.getOutput(result.data!!)
                resultUri?.let {
                    croppedImageUri = it
                    binding.imgProfile.setImageURI(it)
                }
            }

            if (result.resultCode == UCrop.RESULT_ERROR) {
                val error = UCrop.getError(result.data!!)
                error?.printStackTrace()
            }
        }

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
        binding.ivBack.setOnClickListener { findNavController().popBackStack() }
        binding.etDob.setOnClickListener { showDatePicker() }
        binding.btnSignUp.setOnClickListener { handleUpdateMyProfile() }
        binding.imgProfile.setOnClickListener { openGallery() }
        binding.ivCamera.setOnClickListener { openGallery() }
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun startCrop(sourceUri: Uri) {

        val destinationFile = File(requireContext().cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
        val destinationUri = Uri.fromFile(destinationFile)

        val options = UCrop.Options().apply {
            setToolbarTitle("Crop Avatar")
            setFreeStyleCropEnabled(false)
            setHideBottomControls(false)
        }

        val intent = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(500, 500)
            .withOptions(options)
            .getIntent(requireContext())

        cropLauncher.launch(intent)
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

    private fun handleUpdateMyProfile() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phoneNumber = binding.etPhone.text.toString().trim()
        val dateOfBirth = binding.etDob.text.toString().trim()

        resetError()

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
    }

    private fun resetError() {
        binding.layoutFullName.helperText = null
        binding.layoutEmail.helperText = null
        binding.layoutPhone.helperText = null
        binding.layoutDob.helperText = null

        binding.etFullName.setBackgroundResource(R.drawable.edittext_underline)
        binding.etEmail.setBackgroundResource(R.drawable.edittext_underline)
        binding.etPhone.setBackgroundResource(R.drawable.edittext_underline)
        binding.etDob.setBackgroundResource(R.drawable.edittext_underline)
    }
}
