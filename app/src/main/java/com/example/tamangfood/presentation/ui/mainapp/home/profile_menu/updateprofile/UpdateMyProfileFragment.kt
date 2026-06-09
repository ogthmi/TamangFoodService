package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.updateprofile

import android.app.Activity
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.R
import com.example.tamangfood.data.model.user.profile.UserProfileResponse
import com.example.tamangfood.databinding.FragmentUpdateMyProfileBinding
import com.example.tamangfood.presentation.utils.AppPreferences
import com.example.tamangfood.presentation.utils.ImageLoader
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
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
                UCrop.getOutput(result.data!!)?.let {
                    croppedImageUri = it
                    binding.imgProfile.setImageURI(it)
                }
            }
            if (result.resultCode == UCrop.RESULT_ERROR) {
                UCrop.getError(result.data!!)?.printStackTrace()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInitialView()
        setupClickListeners()
        observeViewModelForLoadProfile()
        observeViewModelForUpdateProfile()

        val userId: Int = AppPreferences.getUserId() ?: -1
        viewModel.getUserProfile(userId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupInitialView() {
        binding.etEmail.isEnabled = false
        binding.etEmail.setTextColor(R.color.body_text)
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener { findNavController().popBackStack() }
        binding.etDob.setOnClickListener { showDatePicker() }
        binding.imgProfile.setOnClickListener { openGallery() }
        binding.ivCamera.setOnClickListener { openGallery() }

        binding.btnUpdateProfile.setOnClickListener {
            resetError()

            val fullName = binding.etFullName.text.toString().trim()
            val phoneNumber = binding.etPhone.text.toString().trim()
            val dateOfBirth = binding.etDob.text.toString().trim()
            val imageFile: File? = croppedImageUri?.path?.let { File(it) }

            if (hasValidInput(fullName, phoneNumber, dateOfBirth)) {
                handleUpdateMyProfile(fullName, phoneNumber, dateOfBirth, imageFile)
            }
        }
    }

    private fun observeViewModelForLoadProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userProfileState.collect { state ->
                when (state) {
                    is NetworkState.Init -> Unit

                    is NetworkState.Loading -> setLoading(true)

                    is NetworkState.Success<*> -> {
                        setLoading(false)

                        val response = state.data as UserProfileResponse

                        binding.apply {
                            etFullName.setText(response.result.fullName)
                            etEmail.setText(response.result.email)
                            etPhone.setText(response.result.phoneNumber)
                            etDob.setText(response.result.dateOfBirth.toDisplayDate())
                            progressBar.visibility = View.GONE
                        }

                        ImageLoader.load(requireContext(), binding.imgProfile, response.result.imageUrl)

                        response.result.run {
                            imageUrl.let { url ->
                                ImageLoader.load(requireContext(), binding.imgProfile, url)
                            }
                        }
                    }

                    is NetworkState.Error -> {
                        setLoading(false)
                        binding.progressBar.visibility = View.GONE
                        Utils.showToast(requireContext(), state.message)
                    }
                }
            }
        }
    }

    private fun observeViewModelForUpdateProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateMyProfileState.collect { state ->
                when (state) {
                    is NetworkState.Init -> Unit

                    is NetworkState.Loading -> setLoading(true)

                    is NetworkState.Success<*> -> {
                        setLoading(false)

                        val response = state.data as UserProfileResponse
                        response.result.run {
                            imageUrl.let { url ->
                                ImageLoader.load(requireContext(), binding.imgProfile, url)
                            }
                        }

                        Utils.showToast(requireContext(), "Update user successfully")
                    }

                    is NetworkState.Error -> {
                        setLoading(false)
                        Utils.showToast(requireContext(), state.message)
                    }
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.apply {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnUpdateProfile.isEnabled = !isLoading
            btnUpdateProfile.background = ContextCompat.getDrawable(
                requireContext(),
                if (isLoading) R.drawable.button_disable else R.drawable.button_orange
            )
        }
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
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                binding.etDob.setText("$day/${month + 1}/$year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun hasValidInput(fullName: String, phoneNumber: String, dateOfBirth: String): Boolean {
        var isValid = true
        binding.apply {
            if (fullName.isBlank()) {
                layoutFullName.helperText = "Full name is required"
                etFullName.setBackgroundResource(R.drawable.edittext_error)
                isValid = false
            }
            if (phoneNumber.isEmpty()) {
                layoutPhone.helperText = "Phone number is required"
                etPhone.setBackgroundResource(R.drawable.edittext_error)
                isValid = false
            } else if (phoneNumber.length < 10 || phoneNumber[0] != '0') {
                layoutPhone.helperText = "Invalid phone number"
                etPhone.setBackgroundResource(R.drawable.edittext_error)
                isValid = false
            }
            if (dateOfBirth.isBlank()) {
                layoutDob.helperText = "Date of birth is required"
                etDob.setBackgroundResource(R.drawable.edittext_error)
                isValid = false
            }
        }
        return isValid
    }

    private fun handleUpdateMyProfile(fullName: String, phoneNumber: String, dateOfBirth: String, imageFile: File?) {
        val timestamp = dateOfBirth.toTimestamp()
        if (timestamp == null) {
            binding.apply {
                layoutDob.helperText = "Invalid date of birth"
                etDob.setBackgroundResource(R.drawable.edittext_error)
            }
            return
        }
        viewModel.updateMyProfile(fullName, phoneNumber, timestamp, imageFile)
    }

    private fun resetError() {
        binding.apply {
            layoutFullName.helperText = null
            layoutEmail.helperText = null
            layoutPhone.helperText = null
            layoutDob.helperText = null

            etFullName.setBackgroundResource(R.drawable.edittext_underline)
            etEmail.setBackgroundResource(R.drawable.edittext_underline)
            etPhone.setBackgroundResource(R.drawable.edittext_underline)
            etDob.setBackgroundResource(R.drawable.edittext_underline)
        }
    }
}

private fun String.toDisplayDate(): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(this)
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date!!)
    } catch (e: Exception) {
        this
    }
}

private fun String.toTimestamp(): Long? {
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        sdf.parse(this)?.time
    } catch (e: Exception) {
        null
    }
}