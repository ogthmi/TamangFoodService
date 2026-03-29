package com.example.tamangfood.presentation.ui.mainapp.home.cart.confirmorder.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.domain.model.Address
import com.example.tamangfood.databinding.BottomSheetAddressSelectionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddressSelectionBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetAddressSelectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var addressAdapter: AddressSelectionAdapter
    private var selectedAddressId: Int = -1
    private val addresses = mutableListOf<Address>()

    var onAddressSelected: ((Address) -> Unit)? = null

    companion object {
        const val TAG = "AddressSelectionBottomSheet"
        private const val ARG_SELECTED_ADDRESS_ID = "selected_address_id"

        fun newInstance(selectedAddressId: Int): AddressSelectionBottomSheet {
            return AddressSelectionBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SELECTED_ADDRESS_ID, selectedAddressId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedAddressId = it.getInt(ARG_SELECTED_ADDRESS_ID, -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddressSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadMockAddresses()
        setupRecyclerView()
    }

    private fun loadMockAddresses() {
        addresses.clear()
        addresses.addAll(
            listOf(
                Address(
                    id = 1,
                    name = "My home",
                    fullAddress = "778 Locust View Drive Oakland, CA",
                    latitude = 37.7749,
                    longitude = -122.4194
                ),
                Address(
                    id = 2,
                    name = "My Office",
                    fullAddress = "778 Locust View Drive Oakland, CA",
                    latitude = 37.7849,
                    longitude = -122.4094
                ),
                Address(
                    id = 3,
                    name = "Parent's House",
                    fullAddress = "778 Locust View Drive Oakland, CA",
                    latitude = 37.7649,
                    longitude = -122.4294
                )
            )
        )
    }

    private fun setupRecyclerView() {
        addressAdapter = AddressSelectionAdapter(
            selectedAddressId = selectedAddressId,
            onItemClick = { address ->
                onAddressSelected?.invoke(address)
                dismiss()
            }
        )

        binding.rvAddresses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = addressAdapter
        }
        addressAdapter.submitList(addresses)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}