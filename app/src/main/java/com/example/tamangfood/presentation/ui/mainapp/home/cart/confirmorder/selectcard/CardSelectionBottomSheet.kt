package com.example.tamangfood.presentation.ui.mainapp.home.cart.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.domain.model.Card
import com.example.tamangfood.databinding.BottomSheetCardSelectionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CardSelectionBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetCardSelectionBinding? = null
    private val binding get() = _binding!!

    private var selectedCardId: String? = null
    private lateinit var cards: List<Card>

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

        loadMockCards()
        setupRecycler()

        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    private fun setupRecycler() {
        val adapter = CardSelectionAdapter(
            selectedCardId = selectedCardId,
            onItemClick = { card ->
                onCardSelected?.invoke(card)
                dismiss()
            }
        )

        binding.rvCards.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCards.adapter = adapter

        adapter.submitList(cards)
    }

    private fun loadMockCards() {
        cards = listOf(
            Card(
                paymentMethodId = "pm_mock_1",
                brand = "Visa",
                last4 = "4242",
                expMonth = "12",
                expYear = "2029"
            ),
            Card(
                paymentMethodId = "pm_mock_2",
                brand = "Mastercard",
                last4 = "5454",
                expMonth = "6",
                expYear = "2028"
            ),
            Card(
                paymentMethodId = "pm_mock_3",
                brand = "Amex",
                last4 = "3000",
                expMonth = "9",
                expYear = "2030"
            )
        )
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

