package com.example.tamangfood.presentation.ui.mainapp.home.recommend

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.R
import com.example.tamangfood.databinding.ItemFoodRecommendCardBinding
import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.presentation.utils.ImageLoader

class FoodRecommendAdapter(
    private val onItemClick: (Food) -> Unit,
    private val onFavoriteClick: (Food) -> Unit
) : ListAdapter<Food, FoodRecommendAdapter.FoodRecommendViewHolder>(FoodRecommendDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodRecommendViewHolder {
        val binding = ItemFoodRecommendCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FoodRecommendViewHolder(binding, onItemClick, onFavoriteClick)
    }

    override fun onBindViewHolder(holder: FoodRecommendViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FoodRecommendViewHolder(
        private val binding: ItemFoodRecommendCardBinding,
        private val onItemClick: (Food) -> Unit,
        private val onFavoriteClick: (Food) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(food: Food) {
            binding.tvPrice.text = "$" + food.price.toString()
            binding.tvRating.text = food.avgRating.toString()
            if (!food.urlImage.isNullOrBlank()) {
                ImageLoader.load(binding.root.context, binding.ivFood, food.urlImage)
            } else {
                binding.ivFood.setImageResource(R.drawable.ic_launcher_foreground)
            }
            binding.ivFavorite.apply {
                setImageResource(
                    if (food.hasLiked) R.drawable.ic_heart else R.drawable.ic_heart_outline
                )
                setOnClickListener { onFavoriteClick(food) }
            }
            binding.root.setOnClickListener { onItemClick(food) }
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