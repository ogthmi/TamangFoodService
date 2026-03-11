package com.example.tamangfood.presentation.ui.mainapp.home.cart.confirmorder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.data.model.Food
import com.example.tamangfood.databinding.ItemOrderProductBinding

class ConfirmOrderAdapter(
) : ListAdapter<Food, ConfirmOrderAdapter.OrderViewHolder>(OrderItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(
        private val binding: ItemOrderProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Food) {
            binding.apply {
                ivProductImage.setImageResource(item.imageRes)
                tvProductName.text = item.name
                tvProductPrice.text = item.price
                tvProductQuantity.text = "${item.quantity} items"
            }
        }
    }

    class OrderItemDiffCallback : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem == newItem
        }
    }
}

