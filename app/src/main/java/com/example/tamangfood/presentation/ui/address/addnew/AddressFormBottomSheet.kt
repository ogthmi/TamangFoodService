package com.example.tamangfood.presentation.ui.address.addnew

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.R
import com.example.tamangfood.databinding.BottomSheetAddressFormBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.osmdroid.util.GeoPoint

class AddressFormBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetAddressFormBinding? = null
    private val binding get() = _binding!!
    
    private var selectedLocation: GeoPoint? = null
    private var fullAddress: String? = null

    companion object {
        const val TAG = "AddressFormBottomSheet"
        const val REQUEST_KEY = "address_form_result"

        private const val ARG_LATITUDE = "latitude"
        private const val ARG_LONGITUDE = "longitude"
        private const val ARG_ADDRESS = "address"
        
        fun newInstance(
            location: GeoPoint,
            address: String
        ): AddressFormBottomSheet {
            return AddressFormBottomSheet().apply {
                arguments = Bundle().apply {
                    putDouble(ARG_LATITUDE, location.latitude)
                    putDouble(ARG_LONGITUDE, location.longitude)
                    putString(ARG_ADDRESS, address)
                }
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val latitude = it.getDouble(ARG_LATITUDE)
            val longitude = it.getDouble(ARG_LONGITUDE)
            selectedLocation = GeoPoint(latitude, longitude)
            fullAddress = it.getString(ARG_ADDRESS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddressFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        fullAddress?.let {
            binding.etFullAddress.setText(it)
        }
        
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnApply.setOnClickListener {
            val name = binding.etAddressName.text.toString().trim()
            val address = binding.etFullAddress.text.toString().trim()

            if (name.isEmpty()) {
                binding.addressNameLayout.helperText = "Please enter address name"
                binding.etAddressName.setBackgroundResource(R.drawable.edittext_error)
                return@setOnClickListener
            }

            if (address.isEmpty()) {
                Toast.makeText(requireContext(), "Please select a location", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            selectedLocation?.let { location ->
                dismiss()
                findNavController().popBackStack()
            } ?: run {
                Toast.makeText(requireContext(), "Please select a location on the map", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateAddress(address: String) {
        binding.etFullAddress.setText(address)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

