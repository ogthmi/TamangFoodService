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
import com.example.tamangfood.data.model.Address
import com.example.tamangfood.databinding.FragmentDeliveryAddressBinding
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeliveryAddressFragment : Fragment() {
    private var _binding: FragmentDeliveryAddressBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DeliveryAddressViewModel by viewModels()
    private lateinit var addressAdapter: AddressAdapter
    private lateinit var addressMockData: List<Address>

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
        mockData()

        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        addressAdapter = AddressAdapter(
            onItemClick = { address ->
                findNavController().navigate(DeliveryAddressFragmentDirections.actionDeliveryAddressFragmentToAddNewAddressFragment(true))
            }
        )
        
        binding.rvAddresses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = addressAdapter
        }

        addressAdapter.submitList(addressMockData)
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAddNewAddress.setOnClickListener {
            findNavController().navigate(DeliveryAddressFragmentDirections.actionDeliveryAddressFragmentToAddNewAddressFragment(false))
        }
    }

    private fun mockData(){
        val sampleAddresses = listOf(
            Address(
                id = 1,
                name = "My home",
                fullAddress = "778 Locust View Drive Oakland, CA",
                latitude = 37.7749,
                longitude = -122.4194,
                isSelected = false
            ),
            Address(
                id = 2,
                name = "My Office",
                fullAddress = "778 Locust View Drive Oakland, CA",
                latitude = 37.7849,
                longitude = -122.4094,
                isSelected = true
            ),
            Address(
                id = 3,
                name = "Parent's House",
                fullAddress = "778 Locust View Drive Oakland, CA",
                latitude = 37.7649,
                longitude = -122.4294,
                isSelected = false
            )
        )
        addressMockData = sampleAddresses
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

