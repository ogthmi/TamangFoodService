package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.domain.model.Address
import com.example.tamangfood.databinding.FragmentDeliveryAddressBinding
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        observeAddresses()
    }

    override fun onResume() {
        super.onResume()
        // Reload so that add/update/delete reflects immediately.
        viewModel.loadAddresses()
    }

    private fun observeAddresses() {
        viewModel.loadAddresses()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addressesState.collect { state ->
                when (state) {
                    is NetworkState.Init -> Unit
                    is NetworkState.Loading -> {
                        binding.progressAddresses.visibility = View.VISIBLE
                    }
                    is NetworkState.Success<*> -> {
                        binding.progressAddresses.visibility = View.GONE
                        val list = state.data as List<Address>
                        if(list.isEmpty()){
                            binding.noAddress.visibility = View.VISIBLE
                            binding.rvAddresses.visibility = View.GONE
                        }
                        else{
                            addressAdapter.submitList(list)
                            binding.noAddress.visibility = View.GONE
                            binding.rvAddresses.visibility = View.VISIBLE
                        }
                    }
                    is NetworkState.Error -> {
                        binding.progressAddresses.visibility = View.GONE
                        Utils.showToast(requireContext(), state.message)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        addressAdapter = AddressAdapter(
            onItemClick = { address ->
                findNavController().navigate(
                    DeliveryAddressFragmentDirections.actionDeliveryAddressFragmentToAddNewAddressFragment(
                        true, address.id
                    )
                )
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
            findNavController().navigate(
                DeliveryAddressFragmentDirections.actionDeliveryAddressFragmentToAddNewAddressFragment(
                    false,
                    -1
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
