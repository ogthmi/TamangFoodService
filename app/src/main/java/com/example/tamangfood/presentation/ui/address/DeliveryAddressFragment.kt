package com.example.tamangfood.presentation.ui.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentDeliveryAddressBinding
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeliveryAddressFragment : Fragment() {
    private var _binding: FragmentDeliveryAddressBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DeliveryAddressViewModel by viewModels()
    private lateinit var addressAdapter: AddressAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveryAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
        
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
        
        viewModel.loadAddresses()
    }

    private fun setupRecyclerView() {
        addressAdapter = AddressAdapter(
            onItemClick = { address ->
                // Handle address item click (e.g., edit or show details)
            },
            onSelectClick = { address ->
                viewModel.selectAddress(address)
            }
        )
        
        binding.rvAddresses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = addressAdapter
        }
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAddNewAddress.setOnClickListener {
            findNavController().navigate(R.id.action_deliveryAddressFragment_to_addNewAddressFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.addresses.observe(viewLifecycleOwner) { addresses ->
            addressAdapter.submitList(addresses)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

