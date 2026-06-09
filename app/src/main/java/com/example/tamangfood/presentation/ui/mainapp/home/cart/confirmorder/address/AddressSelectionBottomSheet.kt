package com.example.tamangfood.presentation.ui.mainapp.home.cart.confirmorder.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.domain.model.Address
import com.example.tamangfood.databinding.BottomSheetAddressSelectionBinding
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class AddressSelectionBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetAddressSelectionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddressSelectionViewModel by viewModels()

    private lateinit var addressAdapter: AddressSelectionAdapter
    private var selectedAddressId: Int = -1

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

        setupRecyclerView()
        observeAddresses()
        viewModel.loadAddresses()
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
    }

    private fun observeAddresses() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addressesState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> Unit
                        is NetworkState.Success<*> -> {
                            val addresses = (state.data as? List<*>)?.filterIsInstance<Address>().orEmpty()
                            val mapped = withContext(Dispatchers.IO) {
                                addresses.map { address ->
                                    address.copy(
                                        fullAddress = Utils.resolveFullAddressFromLocation(
                                            context = requireContext(),
                                            latitude = address.latitude,
                                            longitude = address.longitude
                                        )
                                    )
                                }
                            }
                            addressAdapter.submitList(mapped)
                        }
                        is NetworkState.Error -> {
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
}