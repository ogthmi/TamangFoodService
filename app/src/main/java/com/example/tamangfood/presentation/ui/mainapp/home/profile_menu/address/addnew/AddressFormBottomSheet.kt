package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.address.addnew

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import com.example.tamangfood.R
import com.example.tamangfood.databinding.BottomSheetAddressFormBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.osmdroid.util.GeoPoint

class AddressFormBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetAddressFormBinding? = null
    private val binding get() = _binding!!
    
    private var selectedLocation: GeoPoint? = null
    private var fullAddress: String? = null
    private var isDetail: Boolean = false
    private var name: String? = null
    private var addressId: Int = -1

    companion object {
        const val TAG = "AddressFormBottomSheet"
        const val REQUEST_KEY = "address_form_result"

        const val RESULT_ACTION = "result_action"
        const val RESULT_ADDRESS_ID = "result_address_id"

        const val ACTION_ADD = "ADD"
        const val ACTION_UPDATE = "UPDATE"
        const val ACTION_DELETE = "DELETE"

        const val RESULT_LATITUDE = "result_latitude"
        const val RESULT_LONGITUDE = "result_longitude"
        const val RESULT_ADDRESS = "result_address"
        const val RESULT_NAME = "result_name"

        private const val ARG_LATITUDE = "latitude"
        private const val ARG_LONGITUDE = "longitude"
        private const val ARG_ADDRESS = "address"
        private const val ARG_DETAIL = "isDetail"
        private const val ARG_NAME = "name"
        private const val ARG_ADDRESS_ID = "addressId"
        
        fun newInstance(
            location: GeoPoint,
            address: String,
            isDetails: Boolean,
            name: String,
            addressId: Int
        ): AddressFormBottomSheet {
            return AddressFormBottomSheet().apply {
                arguments = Bundle().apply {
                    putDouble(ARG_LATITUDE, location.latitude)
                    putDouble(ARG_LONGITUDE, location.longitude)
                    putString(ARG_ADDRESS, address)
                    putBoolean(ARG_DETAIL, isDetails)
                    putString(ARG_NAME, name)
                    putInt(ARG_ADDRESS_ID, addressId)
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
            isDetail = it.getBoolean(ARG_DETAIL)
            name = it.getString(ARG_NAME)
            addressId = it.getInt(ARG_ADDRESS_ID, -1)
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

        if(isDetail){
            binding.btnDelete.visibility = View.VISIBLE
            binding.btnApply.text = "Update"
            binding.etAddressName.setText(name)
        }
        else{
            binding.btnDelete.visibility = View.GONE
        }
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
                if (isDetail && addressId <= 0) {
                    Toast.makeText(requireContext(), "Invalid address id", Toast.LENGTH_SHORT).show()
                    return@let
                }

                val action = if (isDetail) ACTION_UPDATE else ACTION_ADD
                parentFragmentManager.setFragmentResult(
                    REQUEST_KEY,
                    bundleOf(
                        RESULT_ACTION to action,
                        RESULT_ADDRESS_ID to addressId,
                        RESULT_LATITUDE to location.latitude,
                        RESULT_LONGITUDE to location.longitude,
                        RESULT_ADDRESS to address,
                        RESULT_NAME to name
                    )
                )
                dismiss()
            } ?: run {
                Toast.makeText(requireContext(), "Please select a location on the map", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDelete.setOnClickListener {
            if (addressId <= 0) {
                Toast.makeText(requireContext(), "Invalid address id", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                bundleOf(
                    RESULT_ACTION to ACTION_DELETE,
                    RESULT_ADDRESS_ID to addressId
                )
            )
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

