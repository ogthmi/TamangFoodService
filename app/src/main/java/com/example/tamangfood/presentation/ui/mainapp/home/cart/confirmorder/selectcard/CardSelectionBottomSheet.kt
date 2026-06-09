package com.example.tamangfood.presentation.ui.mainapp.home.cart.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.domain.model.Card
import com.example.tamangfood.databinding.BottomSheetCardSelectionBinding
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CardSelectionBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetCardSelectionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CardSelectionViewModel by viewModels()

    private var selectedCardId: String? = null
    private lateinit var adapter: CardSelectionAdapter

    var onCardSelected: ((Card) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedCardId = arguments?.getString(ARG_SELECTED_CARD_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetCardSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        observePaymentMethods()
        viewModel.loadPaymentMethods()

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun setupRecycler() {
        adapter = CardSelectionAdapter(
            selectedCardId = selectedCardId,
            onItemClick = { card ->
                onCardSelected?.invoke(card)
                dismiss()
            }
        )

        binding.rvCards.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCards.adapter = adapter
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
                            adapter.submitList(cards)
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

    companion object {
        private const val ARG_SELECTED_CARD_ID = "selected_card_id"

        fun newInstance(selectedCardId: String?): CardSelectionBottomSheet {
            return CardSelectionBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_SELECTED_CARD_ID, selectedCardId)
                }
            }
        }

        const val TAG = "CardSelectionBottomSheet"
    }
}

