package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.domain.model.Card
import com.example.tamangfood.databinding.ItemCardBinding

class CardAdapter(
    private val onDeleteClick: (Card) -> Unit
) : ListAdapter<Card, CardAdapter.SavedCardViewHolder>(SavedCardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedCardViewHolder {
        val binding = ItemCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SavedCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SavedCardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SavedCardViewHolder(
        private val binding: ItemCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(card: Card) {
            binding.apply {
                tvCardNumber.text = "**** ${card.last4}"
                tvCardExpiry.text = "Exp: ${card.expMonth}/${card.expYear}"
                ivDelete.setOnClickListener {
                    onDeleteClick(card)
                }
            }
        }
    }

    class SavedCardDiffCallback : DiffUtil.ItemCallback<Card>() {
        override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
            return oldItem.paymentMethodId == newItem.paymentMethodId
        }

        override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
            return oldItem == newItem
        }
    }
}

