package com.example.tamangfood.presentation.ui.mainapp.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.R
import com.example.tamangfood.databinding.ItemFoodBestSellerCardBinding
import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.presentation.utils.ImageLoader

class FoodBestSellerAdapter(
    private val onItemClick: (Food) -> Unit,
) : ListAdapter<Food, FoodBestSellerAdapter.FoodBestSellerViewHolder>(FoodBestSellerDiffCallback()) {

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
        private val onItemClick: (Food) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(food: Food) {
            binding.tvPrice.text = "$" + food.price.toString()
            if (!food.urlImage.isNullOrBlank()) {
                ImageLoader.load(binding.root.context, binding.ivFood, food.urlImage)
            } else {
                binding.ivFood.setImageResource(R.drawable.ic_launcher_foreground)
            }
            binding.ivFood.setOnClickListener { onItemClick(food) }
        }
    }

    class FoodBestSellerDiffCallback : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem == newItem
        }
    }
}
