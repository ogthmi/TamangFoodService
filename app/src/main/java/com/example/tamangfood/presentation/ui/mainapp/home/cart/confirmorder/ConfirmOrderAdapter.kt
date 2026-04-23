package com.example.tamangfood.presentation.ui.mainapp.home.cart.confirmorder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.R
import com.example.tamangfood.domain.model.CartItem
import com.example.tamangfood.databinding.ItemOrderProductBinding

class ConfirmOrderAdapter(
    private val onItemClick: (CartItem) -> Unit,
) : ListAdapter<CartItem, ConfirmOrderAdapter.OrderViewHolder>(OrderItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(
        private val binding: ItemOrderProductBinding,
        onItemClick: (CartItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            val ingredientDetails = item.ingredients.joinToString("\n") { ingredient ->
                "${ingredient.name}: $${ingredient.price}"
            }
            val ingredientPrice = item.ingredients.sumOf { it.price }
            val unitPrice = item.food.price + ingredientPrice
            val lineTotal = unitPrice * item.quantity
            binding.apply {
                ivProductImage.setImageResource(R.drawable.ic_launcher_background)
                tvProductName.text = item.food.name
                tvProductPrice.text = String.format("$%.2f", lineTotal.toDouble())
                tvProductQuantity.text = "${item.quantity} items"
                if(ingredientDetails.isBlank()){
                    tvProductIngredients.text = ingredientDetails
                }
                else {
                    tvProductIngredients.visibility = View.GONE
                }
                root.setOnClickListener { onItemClick(item) }
            }
        }
    }

    class OrderItemDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}

