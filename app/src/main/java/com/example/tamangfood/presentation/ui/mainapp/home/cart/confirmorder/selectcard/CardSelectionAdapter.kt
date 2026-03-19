package com.example.tamangfood.presentation.ui.mainapp.home.cart.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.data.model.payment.Card
import com.example.tamangfood.databinding.ItemCardSelectionBinding

class CardSelectionAdapter(
    private val selectedCardId: String?,
    private val onItemClick: (Card) -> Unit
) : ListAdapter<Card, CardSelectionAdapter.CardViewHolder>(CardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = ItemCardSelectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(getItem(position), selectedCardId == getItem(position).paymentMethodId)
    }

    inner class CardViewHolder(
        private val binding: ItemCardSelectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(card: Card, isSelected: Boolean) {
            binding.tvCardBrand.text = card.brand
            binding.tvCardLast4.text = "•••• ${card.last4}"
            binding.ivSelected.visibility = if (isSelected) android.view.View.VISIBLE else android.view.View.GONE

            binding.root.setOnClickListener {
                onItemClick(card)
            }
        }
    }

    class CardDiffCallback : DiffUtil.ItemCallback<Card>() {
        override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
            return oldItem.paymentMethodId == newItem.paymentMethodId
        }

        override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
            return oldItem == newItem
        }
    }
}

