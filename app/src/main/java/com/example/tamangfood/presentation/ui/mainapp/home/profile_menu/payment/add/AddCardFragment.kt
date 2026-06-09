package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.payment.add

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentAddCardBinding
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.PaymentMethodCreateParams
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddCardFragment : Fragment() {
    private var _binding: FragmentAddCardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddCardViewModel by viewModels()
    private lateinit var stripe: Stripe

    companion object {
        private const val TAG = "AddCardFragment"
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
        stripe = Stripe(
            requireContext(),
            PaymentConfiguration.getInstance(requireContext()).publishableKey
        )

        Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAddCard.setOnClickListener {
            addCard()
        }

        observeCreatePaymentMethod()
    }

    private fun setLoading(isLoading: Boolean) {
        binding.pbAddCard.isVisible = isLoading
        binding.btnAddCard.isEnabled = !isLoading
    }

    private fun addCard() {
        val params: PaymentMethodCreateParams =
            binding.cardFormView.paymentMethodCreateParams ?: run {
                Utils.showToast(requireContext(), getString(R.string.please_enter_card_details))
                Log.w(TAG, "Cannot create PaymentMethod: invalid or incomplete card form")
                return
            }

        setLoading(true)
        stripe.createPaymentMethod(
            params,
            callback = object : ApiResultCallback<PaymentMethod> {
                override fun onSuccess(result: PaymentMethod) {
                    val paymentMethodId = result.id
                    Log.d(TAG, "PaymentMethod created: id=$paymentMethodId, type=${result.type}")
                    if (paymentMethodId.isNullOrBlank()) {
                        setLoading(false)
                        Utils.showToast(requireContext(), "Invalid payment method id")
                        return
                    }
                    viewModel.savePaymentMethod(paymentMethodId)
                }

                override fun onError(e: Exception) {
                    setLoading(false)
                    Log.e(TAG, "Create PaymentMethod failed: ${e.message}", e)
                    Utils.showToast(requireContext(), e.message ?: "Unable to add card")
                }
            }
        )
    }

    private fun observeCreatePaymentMethod() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createPaymentMethodState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> setLoading(true)
                        is NetworkState.Success<*> -> {
                            setLoading(false)
                            Utils.showToast(requireContext(), "Card added successfully")
                            viewModel.resetCreatePaymentMethodState()
                            findNavController().popBackStack()
                        }
                        is NetworkState.Error -> {
                            setLoading(false)
                            Log.e(TAG, "Save payment method failed: ${state.message}")
                            Utils.showToast(requireContext(), state.message)
                            viewModel.resetCreatePaymentMethodState()
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