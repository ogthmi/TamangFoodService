package com.example.tamangfood.presentation.ui.mainapp.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.R
import com.example.tamangfood.data.model.CartItem
import com.example.tamangfood.databinding.ItemFoodCartBinding

class CartAdapter(
    private val onQuantityChange: (CartItem, Int) -> Unit,
    private val onItemClick: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemFoodCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(
        private val binding: ItemFoodCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.apply {
                ivFoodImage.setImageResource(item.food.imageRes)
                tvFoodName.text = item.food.name
                tvFoodPrice.text = item.food.price
                tvDate.text = item.dateTime
                tvTime.text = item.dateTime
                tvQuantity.text = item.quantity.toString()
                checkIconQuantity(item)

                root.setOnClickListener {
                    onItemClick(item)
                }

                ivDecrease.setOnClickListener {
                    if (item.quantity > 1) {
                        val newQuantity = item.quantity - 1
                        onQuantityChange(item, newQuantity)
                    }
                }

                ivIncrease.setOnClickListener {
                    if (item.quantity < item.food.quantity) {
                        val newQuantity = item.quantity + 1
                        onQuantityChange(item, newQuantity)
                    }
                }
            }
        }

        private fun checkIconQuantity(item: CartItem) {
            if (item.quantity == item.food.quantity) {
                binding.ivIncrease.setImageResource(R.drawable.ic_plus_unactive)
            } else {
                binding.ivIncrease.setImageResource(R.drawable.ic_plus_active)
            }

            if (item.quantity == 1) {
                binding.ivDecrease.setImageResource(R.drawable.ic_minus_unactive)
            } else {
                binding.ivDecrease.setImageResource(R.drawable.ic_minus_active)
            }
        }
    }

    class CartItemDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}

