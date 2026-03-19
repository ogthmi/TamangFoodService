package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.payment.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.BuildConfig
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentAddCardBinding
import com.example.tamangfood.presentation.utils.Utils
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.model.PaymentMethodCreateParams

class AddCardFragment : Fragment() {
    private var _binding: FragmentAddCardBinding? = null
    private val binding get() = _binding!!

    private var publishableKey: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        publishableKey = BuildConfig.STRIPE_PUBLISHABLE_KEY
        if (publishableKey.isNotBlank()) {
            PaymentConfiguration.Companion.init(requireContext(), publishableKey)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAddCard.setOnClickListener {
            addCard()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.pbAddCard.isVisible = isLoading
        binding.btnAddCard.isEnabled = !isLoading
    }

    private fun addCard() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}