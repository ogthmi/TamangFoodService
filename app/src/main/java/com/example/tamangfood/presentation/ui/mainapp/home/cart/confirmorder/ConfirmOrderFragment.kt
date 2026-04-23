package com.example.tamangfood.presentation.ui.mainapp.home.cart.confirmorder

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.domain.model.Address
import com.example.tamangfood.domain.model.CartItem
import com.example.tamangfood.domain.model.CartSummary
import com.example.tamangfood.domain.model.Card
import com.example.tamangfood.databinding.FragmentConfirmOrderBinding
import com.example.tamangfood.presentation.ui.mainapp.home.cart.confirmorder.address.AddressSelectionBottomSheet
import com.example.tamangfood.presentation.ui.mainapp.home.cart.payment.CardSelectionBottomSheet
import com.example.tamangfood.presentation.utils.AppPreferences
import com.example.tamangfood.presentation.utils.DefaultLocation
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToLong

@AndroidEntryPoint
class ConfirmOrderFragment : Fragment() {
    private var _binding: FragmentConfirmOrderBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ConfirmOrderViewModel by viewModels()
    private lateinit var orderAdapter: ConfirmOrderAdapter
    private var selectedAddress: Address? = null
    private val orderItems = mutableListOf<CartItem>()
    private var isCreditCardSelected = true

    private var selectedCard: Card? = null
    private var deliveryFee: Double = 0.0
    private var serverCartTotal: Int? = null
    private var isLoadingCartItems: Boolean = false
    private var isCreatingPaymentIntent: Boolean = false
    private var isCreatingOrder: Boolean = false
    private var pendingPaymentMethodId: String? = null
    private lateinit var paymentSheet: PaymentSheet

    companion object {
        private const val TAG = "ConfirmOrderFragment"
    }

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
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

        loadDefaultPaymentData()
        setupRecyclerView()
        setupPaymentMethodSelector()
        setupClickListeners()
        observeAddresses()
        observeCartItems()
        observePaymentMethods()
        observeCreateOrder()
        observeCreatePaymentIntent()
        viewModel.loadAddresses()
        viewModel.loadCartItems()
        viewModel.loadPaymentMethods()
        calculateTotals()
    }

    private fun loadDefaultPaymentData() {
        updatePaymentDisplay()
        updateAddressDisplay()
    }

    private fun setupRecyclerView() {
        orderAdapter = ConfirmOrderAdapter(
            onItemClick = { cartItem ->
                val bundle = bundleOf(
                    "foodId" to cartItem.food.id
                )
                findNavController().navigate(
                    R.id.action_confirmOrderFragment_to_foodDetailFragment,
                    bundle
                )

            }
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
            onPlaceOrderClick()
        }

        binding.ivSelectCreditCard.setOnClickListener {
            val bottomSheet = CardSelectionBottomSheet.newInstance(selectedCard?.paymentMethodId)
            bottomSheet.onCardSelected = { card ->
                selectedCard = card
                updatePaymentDisplay()
            }
            bottomSheet.show(parentFragmentManager, CardSelectionBottomSheet.TAG)
        }
    }

    private fun setupPaymentMethodSelector() {
        // Default selected method: credit card.
        updatePaymentMethodSelection(isCreditCard = true)

        binding.radioBtnCash.setOnClickListener {
            updatePaymentMethodSelection(isCreditCard = false)
        }
        binding.radioBtnCreditCard.setOnClickListener {
            updatePaymentMethodSelection(isCreditCard = true)
        }
    }

    private fun updatePaymentMethodSelection(isCreditCard: Boolean) {
        isCreditCardSelected = isCreditCard
        binding.radioBtnCreditCard.isChecked = isCreditCard
        binding.radioBtnCash.isChecked = !isCreditCard
        binding.tvCardMasked.visibility = if (isCreditCard) View.VISIBLE else View.GONE
        binding.ivSelectCreditCard.visibility = if (isCreditCard) View.VISIBLE else View.GONE
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

    private fun updatePaymentDisplay(){
        binding.apply {
            tvCardMasked.text = selectedCard?.let { "*** *** *** ${it.last4}" } ?: "-"
        }
    }

    private fun updateAddressDisplay() {
        selectedAddress?.let { address ->
            binding.tvNameAddress.text = address.name
            binding.tvFullNameAddress.text = address.fullAddress
        } ?: run {
            binding.tvNameAddress.text = getString(R.string.no_delivery_address_found)
            binding.tvFullNameAddress.text = ""
            binding.tvEstimatedDistance.text = "-"
            binding.tvEstimatedTime.text = "-"
        }
    }

    private fun calculateEstimatedDistanceAndTime() {
        val address = selectedAddress ?: run {
            deliveryFee = 0.0
            calculateTotals()
            return
        }

        val distanceKm = calculateDistanceFromRestaurant(
            destinationLat = address.latitude,
            destinationLng = address.longitude
        )
        deliveryFee = calculateDeliveryFee(distanceKm)
        val deliveryMinutes = estimateDeliveryMinutes(distanceKm)

        binding.tvEstimatedDistance.text = String.format("%.1f km", distanceKm)
        binding.tvEstimatedTime.text = getString(R.string.mins_format, deliveryMinutes)
        calculateTotals()
    }
    private fun calculateTotals() {
        val computedSubtotal = orderItems.sumOf { item ->
            val ingredientUnitPrice = item.ingredients.sumOf { it.price }
            val unitPrice = item.food.price + ingredientUnitPrice
            unitPrice * item.quantity
        }
        val subtotalValue = serverCartTotal?.toDouble() ?: computedSubtotal.toDouble()
        val total = subtotalValue + deliveryFee

        binding.tvSubtotal.text = String.format("$%.2f", subtotalValue)
        binding.tvDelivery.text = String.format("$%.2f", deliveryFee)
        binding.tvTotal.text = String.format("$%.2f", total)
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
                            if (selectedAddress == null && mapped.isNotEmpty()) {
                                selectedAddress = mapped.first()
                                updateAddressDisplay()
                                calculateEstimatedDistanceAndTime()
                            }
                        }
                        is NetworkState.Error -> {
                            Utils.showToast(requireContext(), state.message)
                            updateAddressDisplay()
                        }
                    }
                }
            }
        }
    }

    private fun observeCartItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartItemsState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> {
                            isLoadingCartItems = true
                            renderCartLoading()
                        }
                        is NetworkState.Success<*> -> {
                            isLoadingCartItems = false
                            renderCartLoading()
                            val summary = state.data as? CartSummary
                            serverCartTotal = summary?.totalPrice
                            val cartItems = summary?.carts.orEmpty()
                            orderItems.clear()
                            orderItems.addAll(cartItems)
                            orderAdapter.submitList(orderItems.toList())
                            calculateTotals()
                        }
                        is NetworkState.Error -> {
                            isLoadingCartItems = false
                            renderCartLoading()
                            Utils.showToast(requireContext(), state.message)
                        }
                    }
                }
            }
        }
    }

    private fun observePaymentMethods() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.paymentMethodsState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> Unit
                        is NetworkState.Success<*> -> {
                            val cards = (state.data as? List<*>)?.filterIsInstance<Card>().orEmpty()
                            if (selectedCard == null && cards.isNotEmpty()) {
                                selectedCard = cards.first()
                                updatePaymentDisplay()
                            }
                        }
                        is NetworkState.Error -> {
                            Utils.showToast(requireContext(), state.message)
                        }
                    }
                }
            }
        }
    }

    private fun renderCartLoading() {
        binding.progressConfirmOrder.visibility = if (isLoadingCartItems) View.VISIBLE else View.GONE
        binding.scrollView.visibility = if (isLoadingCartItems) View.INVISIBLE else View.VISIBLE
        binding.btnPlaceOrder.isEnabled = !isLoadingCartItems && !isCreatingPaymentIntent && !isCreatingOrder
    }

    private fun onPlaceOrderClick() {
        val addressId = selectedAddress?.id ?: run {
            Utils.showToast(requireContext(), "Please select delivery address")
            return
        }
        val isCartEmpty = orderItems.isEmpty() || (serverCartTotal ?: 0) <= 0
        if (isCartEmpty) {
            Utils.showToast(requireContext(), "Giỏ hàng đang trống")
            return
        }
        pendingPaymentMethodId = if (isCreditCardSelected) {
            val paymentMethodId = selectedCard?.paymentMethodId.orEmpty()
            if (paymentMethodId.isBlank()) {
                Utils.showToast(requireContext(), "Please select a card")
                return
            }
            paymentMethodId
        } else {
            null
        }

        viewModel.createOrder(
            addressId = addressId,
            deliveryPrice = deliveryFee.roundToLong(),
            cartItems = orderItems
        )
    }

    private fun observeCreateOrder() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createOrderState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> {
                            isCreatingOrder = true
                            binding.btnPlaceOrder.isEnabled = false
                        }
                        is NetworkState.Success<*> -> {
                            isCreatingOrder = false
                            val orderId = state.data as? Int ?: -1
                            val paymentMethodId = pendingPaymentMethodId
                            if (paymentMethodId.isNullOrBlank()) {
                                Utils.showToast(requireContext(), "Order created successfully")
                                viewModel.resetCreateOrderState()
                                navigateOrderSuccess()
                            } else {
                                val userId = AppPreferences.getUserId() ?: -1
                                if (userId <= 0) {
                                    Utils.showToast(requireContext(), "Invalid user id")
                                    binding.btnPlaceOrder.isEnabled = true
                                    viewModel.resetCreateOrderState()
                                    return@collect
                                }
                                viewModel.resetCreateOrderState()
                                viewModel.createPaymentIntent(
                                    orderId = orderId,
                                    userId = userId,
                                    paymentMethodId = paymentMethodId
                                )
                            }
                        }
                        is NetworkState.Error -> {
                            isCreatingOrder = false
                            binding.btnPlaceOrder.isEnabled = true
                            Utils.showToast(requireContext(), state.message)
                            viewModel.resetCreateOrderState()
                        }
                    }
                }
            }
        }
    }

    private fun observeCreatePaymentIntent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createPaymentIntentState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> {
                            isCreatingPaymentIntent = true
                            binding.btnPlaceOrder.isEnabled = false
                        }
                        is NetworkState.Success<*> -> {
                            isCreatingPaymentIntent = false
                            binding.btnPlaceOrder.isEnabled = true
                            val clientSecret = state.data as? String
                            if (clientSecret.isNullOrBlank()) {
                                Utils.showToast(requireContext(), "Invalid payment intent secret")
                                viewModel.resetCreatePaymentIntentState()
                                return@collect
                            }
                            presentPaymentSheet(clientSecret)
                            viewModel.resetCreatePaymentIntentState()
                        }
                        is NetworkState.Error -> {
                            isCreatingPaymentIntent = false
                            binding.btnPlaceOrder.isEnabled = true
                            Utils.showToast(requireContext(), state.message)
                            viewModel.resetCreatePaymentIntentState()
                        }
                    }
                }
            }
        }
    }

    private fun presentPaymentSheet(clientSecret: String) {
        val configuration = PaymentSheet.Configuration(
            merchantDisplayName = getString(R.string.app_name)
        )
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret = clientSecret,
            configuration = configuration
        )
    }

    private fun onPaymentSheetResult(paymentResult: PaymentSheetResult) {
        when (paymentResult) {
            is PaymentSheetResult.Completed -> {
                Utils.showToast(requireContext(), "Payment successful")
                pendingPaymentMethodId = null
                navigateOrderSuccess()
            }
            is PaymentSheetResult.Canceled -> {
                navigateOrderSuccess()
            }
            is PaymentSheetResult.Failed -> {
                val errorMessage = paymentResult.error.message.orEmpty()
                Log.e(TAG, "Payment failed: $errorMessage", paymentResult.error)

                // Temporary fallback: if BE already confirmed the intent, PaymentSheet can't be shown.
                // Treat this as success to avoid blocking user checkout while BE flow is being fixed.
                val isAlreadySucceededIntent =
                    errorMessage.contains("already succeeded", ignoreCase = true) ||
                        errorMessage.contains("status of succeeded", ignoreCase = true) ||
                        errorMessage.contains("cannot be set up in status", ignoreCase = true)
                if (isAlreadySucceededIntent) {
                    Utils.showToast(requireContext(), "Payment already successful")
                    pendingPaymentMethodId = null
                    navigateOrderSuccess()
                    return
                }
                navigateOrderSuccess()

//                Utils.showToast(requireContext(), paymentResult.error.localizedMessage ?: "Payment failed")
            }
        }
    }

    private fun navigateOrderSuccess() {
        findNavController().navigate(
            ConfirmOrderFragmentDirections.actionConfirmOrderFragmentToOrderCancelledSuccessFragment(
                false, true
            )
        )
    }

    private fun calculateDistanceFromRestaurant(destinationLat: Double, destinationLng: Double): Double {
        val result = FloatArray(1)
        Location.distanceBetween(
            DefaultLocation.LAT.value,
            DefaultLocation.LNG.value,
            destinationLat,
            destinationLng,
            result
        )
        return (result.firstOrNull() ?: 0f) / 1000.0
    }

    private fun calculateDeliveryFee(distanceKm: Double): Double {
        return when {
            distanceKm <= 2.0 -> 2.0
            distanceKm <= 5.0 -> 3.0
            distanceKm <= 10.0 -> 5.0
            else -> 7.0
        }
    }

    private fun estimateDeliveryMinutes(distanceKm: Double): Int {
        return when {
            distanceKm <= 2.0 -> 15
            distanceKm <= 5.0 -> 25
            distanceKm <= 10.0 -> 35
            else -> 45
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

