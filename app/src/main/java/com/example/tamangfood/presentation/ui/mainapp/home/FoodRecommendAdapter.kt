package com.example.tamangfood.presentation.ui.mainapp.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.data.model.Food
import com.example.tamangfood.databinding.ItemFoodRecommendCardBinding


class FoodRecommendAdapter(
    private val onItemClick: (Food) -> Unit,
) : ListAdapter<Food, FoodRecommendAdapter.FoodRecommendViewHolder>(FoodRecommendDiffCallback()) {

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
        private val onItemClick: (Food) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(food: Food) {
            binding.tvPrice.text = food.price
            binding.ivFood.setImageResource(food.imageRes)

            binding.ivFood.setOnClickListener { onItemClick(food) }
        }
    }

    class FoodRecommendDiffCallback : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem == newItem
        }
    }
}