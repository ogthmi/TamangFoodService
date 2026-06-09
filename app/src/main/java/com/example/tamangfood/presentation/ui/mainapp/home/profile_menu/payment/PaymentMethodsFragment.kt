package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.payment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.domain.model.Card
import com.example.tamangfood.databinding.FragmentPaymentMethodsBinding
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaymentMethodsFragment : Fragment() {
    private var _binding: FragmentPaymentMethodsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PaymentMethodsViewModel by viewModels()

    private lateinit var cardAdapter: CardAdapter
    private var cardList: List<Card> = emptyList()

    companion object {
        private const val TAG = "PaymentMethodsFragment"
    }
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

        setupRecyclerView()
        setupClickListener()
        observePaymentMethods()
        observeDeletePaymentMethod()
        viewModel.loadPaymentMethods()
    }

    private fun setupRecyclerView(){
        cardAdapter = CardAdapter(
            onDeleteClick = { card ->
                if (card.paymentMethodId.isNotBlank()) {
                    showDeleteConfirmDialog(card.paymentMethodId)
                }
            }
        )

        binding.rvCard.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cardAdapter
        }
        cardAdapter.submitList(emptyList())
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


    private fun observePaymentMethods() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.paymentMethodsState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> {
                            binding.tvEmptyState.isVisible = false
                        }
                        is NetworkState.Success<*> -> {
                            cardList = (state.data as? List<*>)?.filterIsInstance<Card>().orEmpty()
                            cardAdapter.submitList(cardList)
                            binding.tvEmptyState.isVisible = cardList.isEmpty()
                            viewModel.resetPaymentMethodsState()
                        }
                        is NetworkState.Error -> {
                            Log.e(TAG, "Load payment methods failed: ${state.message}")
                            binding.tvEmptyState.isVisible = true
                            Utils.showToast(requireContext(), state.message)
                            viewModel.resetPaymentMethodsState()
                        }
                    }
                }
            }
        }
    }

    private fun observeDeletePaymentMethod() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deletePaymentMethodState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> Unit
                        is NetworkState.Success<*> -> {
                            Utils.showToast(requireContext(), "Delete card successful!")
                            viewModel.resetDeletePaymentMethodState()
                            viewModel.loadPaymentMethods()
                        }
                        is NetworkState.Error -> {
                            Log.e(TAG, "Delete payment method failed: ${state.message}")
                            Utils.showToast(requireContext(), state.message)
                            viewModel.resetDeletePaymentMethodState()
                        }
                    }
                }
            }
        }
    }

    private fun showDeleteConfirmDialog(paymentMethodId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete card")
            .setMessage("Are you sure you want to delete this card?")
            .setNegativeButton(R.string.delete_account_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.delete) { dialog, _ ->
                dialog.dismiss()
                viewModel.deletePaymentMethod(paymentMethodId)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

