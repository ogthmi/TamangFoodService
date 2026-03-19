package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.data.model.payment.Card
import com.example.tamangfood.databinding.FragmentPaymentMethodsBinding
import com.example.tamangfood.presentation.utils.Utils

class PaymentMethodsFragment : Fragment() {
    private var _binding: FragmentPaymentMethodsBinding? = null
    private val binding get() = _binding!!

    private lateinit var cardAdapter: CardAdapter
    private lateinit var cardList: List<Card>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentMethodsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))

        mockData()
        setupRecyclerView()
        setupClickListener()
    }

    private fun setupRecyclerView(){
        cardAdapter = CardAdapter(
            onDeleteClick = { card -> }
        )

        binding.rvCard.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cardAdapter
        }
        cardAdapter.submitList(cardList)
    }

    private fun setupClickListener(){

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAddNewCard.setOnClickListener {
            findNavController().navigate(
                PaymentMethodsFragmentDirections.actionPaymentMethodsFragmentToAddCardFragment()
            )
        }
    }


    private fun mockData() {
        cardList = listOf(
            Card(
                paymentMethodId = "pm_mock_1",
                brand = "Visa",
                last4 = "4242",
                expMonth = 12,
                expYear = 2029
            ),
            Card(
                paymentMethodId = "pm_mock_2",
                brand = "Mastercard",
                last4 = "5454",
                expMonth = 6,
                expYear = 2028
            ),
            Card(
                paymentMethodId = "pm_mock_3",
                brand = "Amex",
                last4 = "3000",
                expMonth = 9,
                expYear = 2030
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

