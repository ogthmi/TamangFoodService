package com.example.tamangfood.presentation.ui.mainapp.home.cart.confirmorder

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
import com.example.tamangfood.data.model.CartItem
import com.example.tamangfood.data.model.Food
import com.example.tamangfood.databinding.FragmentConfirmOrderBinding
import com.example.tamangfood.presentation.utils.FoodType
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmOrderFragment : Fragment() {
    private var _binding: FragmentConfirmOrderBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ConfirmOrderViewModel by viewModels()
    private lateinit var orderAdapter: ConfirmOrderAdapter
    private var selectedAddress: Address? = null
    private val orderItems = mutableListOf<Food>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
        
        loadMockData()
        setupRecyclerView()
        setupClickListeners()
        calculateTotals()
    }

    private fun loadMockData() {
        // TODO: Load from CartFragment or ViewModel
        orderItems.clear()
        orderItems.addAll(
            listOf(
                Food(3,
                    "Chicken Curry",
                    "$25.00",
                    1,
                    3.5,
                    FoodType.MEAL,
                    imageRes = R.drawable.ic_launcher_background),
                Food(4,
                    "Rice Bowl",
                    "$25.00",
                    1,
                    3.0,
                    FoodType.SNACK,
                    imageRes = R.drawable.ic_launcher_background),
                Food(5,
                    "Bean and Vegetable Burger",
                    "$25.00",
                    1,
                    4.0,
                    FoodType.VEGAN,
                    imageRes = R.drawable.ic_launcher_background),
            )
        )
        
        // Set default address
        selectedAddress = Address(
            id = 1,
            name = "My home",
            fullAddress = "778 Locust View Drive Oakland, CA",
            latitude = 37.7749,
            longitude = -122.4194
        )
        updateAddressDisplay()
        calculateEstimatedDistanceAndTime()
    }

    private fun setupRecyclerView() {
        orderAdapter = ConfirmOrderAdapter(
        )

        binding.rvOrderItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }
        orderAdapter.submitList(orderItems.toList())
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivEditAddress.setOnClickListener {
            showAddressSelectionBottomSheet()
        }

        binding.btnPlaceOrder.setOnClickListener {
            // TODO: Place order
            findNavController().navigate(ConfirmOrderFragmentDirections.actionConfirmOrderFragmentToOrderCancelledSuccessFragment(
                false,
                true
            ))
        }
    }

    private fun showAddressSelectionBottomSheet() {
        val bottomSheet = AddressSelectionBottomSheet.newInstance(
            selectedAddress?.id ?: -1
        )
        bottomSheet.onAddressSelected = { address ->
            selectedAddress = address
            updateAddressDisplay()
            calculateEstimatedDistanceAndTime()
        }
        bottomSheet.show(parentFragmentManager, AddressSelectionBottomSheet.TAG)
    }

    private fun updateAddressDisplay() {
        selectedAddress?.let { address ->
            binding.tvNameAddress.text = address.name
            binding.tvFullNameAddress.text = address.fullAddress
        }
    }

    private fun calculateEstimatedDistanceAndTime() {
        // TODO: Calculate based on selected address and restaurant location
        // For now, using mock data
        binding.tvEstimatedDistance.text = "2.5 km"
        binding.tvEstimatedTime.text = "15-20 min"
    }
    private fun calculateTotals() {
        val subtotal = orderItems.sumOf { item ->
            val price = item.price.replace("$", "").toDoubleOrNull() ?: 0.0
            price * item.quantity
        }

        // Mockdata
        val taxAndFees = subtotal * 0.15 // 15% tax
        val delivery = 3.0
        val total = subtotal + taxAndFees + delivery

        binding.tvSubtotal.text = String.format("$%.2f", subtotal)
        binding.tvTaxAndFees.text = String.format("$%.2f", taxAndFees)
        binding.tvDelivery.text = String.format("$%.2f", delivery)
        binding.tvTotal.text = String.format("$%.2f", total)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

