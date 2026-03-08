package com.example.tamangfood.presentation.ui.mainapp.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.databinding.ItemFoodBestSellerCardBinding
import com.example.tamangfood.databinding.ItemFoodRecommendCardBinding


class FoodRecommendAdapter(
    private val onItemClick: (FoodItem) -> Unit,
) : androidx.recyclerview.widget.ListAdapter<FoodItem, FoodRecommendAdapter.FoodRecommendViewHolder>(FoodRecommendDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodRecommendViewHolder {
        val binding = ItemFoodRecommendCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FoodRecommendViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: FoodRecommendViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FoodRecommendViewHolder(
        private val binding: ItemFoodRecommendCardBinding,
        private val onItemClick: (FoodItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(food: FoodItem) {
            binding.tvPrice.text = food.price
            binding.ivFood.setImageResource(food.imageRes)
        }
    }

    class FoodRecommendDiffCallback : DiffUtil.ItemCallback<FoodItem>() {
        override fun areItemsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
            return oldItem == newItem
        }
    }
}