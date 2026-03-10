package com.example.tamangfood.presentation.ui.mainapp.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.data.model.Food
import com.example.tamangfood.databinding.ItemOrderProductBinding

class OrderItemAdapter : ListAdapter<Food, OrderItemAdapter.OrderItemViewHolder>(
    OrderItemDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = ItemOrderProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderItemViewHolder(
        private val binding: ItemOrderProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Food) {
            binding.apply {
                tvProductName.text = item.name
                tvProductPrice.text = item.price
                tvProductQuantity.text = "x${item.quantity}"
                ivProductImage.setImageResource(item.imageRes)
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
