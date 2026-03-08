package com.example.tamangfood.presentation.ui.mainapp.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.databinding.ItemFoodBestSellerCardBinding


class FoodBestSellerAdapter(
    private val onItemClick: (FoodItem) -> Unit,
) : androidx.recyclerview.widget.ListAdapter<FoodItem, FoodBestSellerAdapter.FoodBestSellerViewHolder>(FoodBestSellerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodBestSellerViewHolder {
        val binding = ItemFoodBestSellerCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FoodBestSellerViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: FoodBestSellerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FoodBestSellerViewHolder(
        private val binding: ItemFoodBestSellerCardBinding,
        private val onItemClick: (FoodItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(food: FoodItem) {
            binding.tvPrice.text = food.price
            binding.ivFood.setImageResource(food.imageRes)
        }
    }

    class FoodBestSellerDiffCallback : DiffUtil.ItemCallback<FoodItem>() {
        override fun areItemsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem == newItem
        }
    }
}