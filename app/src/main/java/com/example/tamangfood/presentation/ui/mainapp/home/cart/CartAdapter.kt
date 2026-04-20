package com.example.tamangfood.presentation.ui.mainapp.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.R
import com.example.tamangfood.databinding.ItemFoodCartBinding
import com.example.tamangfood.domain.model.CartItem
import com.example.tamangfood.presentation.utils.ImageLoader

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
                if (!item.food.urlImage.isNullOrBlank()) {
                    ImageLoader.load(root.context, ivFoodImage, item.food.urlImage)
                } else {
                    binding.ivFoodImage.setImageResource(R.drawable.ic_launcher_foreground)
                }
                tvFoodName.text = item.food.name
                tvFoodPrice.text = String.format("$%d", item.food.price)
                val ingredientSummaries = item.ingredients
                    .mapNotNull { ingredient ->
                        val name = ingredient.name.trim()
                        if (name.isEmpty()) {
                            null
                        } else {
                            "$name: $${ingredient.price}"
                        }
                    }

                if (ingredientSummaries.isEmpty()) {
                    tvIngredients.visibility = android.view.View.GONE
                } else {
                    tvIngredients.visibility = android.view.View.VISIBLE
                    tvIngredients.text = ingredientSummaries.joinToString(separator = "\n")
                }
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
            if (item.quantity >= item.food.quantity) {
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

