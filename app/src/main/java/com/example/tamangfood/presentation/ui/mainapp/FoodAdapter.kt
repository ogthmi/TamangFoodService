package com.example.tamangfood.presentation.ui.mainapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.R
import com.example.tamangfood.databinding.ItemFoodBinding
import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.presentation.utils.FoodType
import com.example.tamangfood.presentation.utils.ImageLoader

class FoodAdapter(
    private val onItemClick: (Food) -> Unit,
    private val onFavoriteClick: (Food) -> Unit,
    private val onAddToCartClick: (Food) -> Unit,
) : ListAdapter<Food, FoodAdapter.FoodItemViewHolder>(FoodItemDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val binding = ItemFoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FoodItemViewHolder(binding, onItemClick, onFavoriteClick, onAddToCartClick)
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FoodItemViewHolder(
        private val binding: ItemFoodBinding,
        private val onItemClick: (Food) -> Unit,
        private val onFavoriteClick: (Food) -> Unit,
        private val onAddToCartClick: (Food) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Food) {
            binding.tvTitle.text = item.name
            binding.tvSubtitle.text = item.description.orEmpty()
            binding.tvPrice.text = "$" + item.price.toString()
            binding.tvRating.text = item.avgRating.toString()
            if (!item.urlImage.isNullOrBlank()) {
                ImageLoader.load(binding.root.context, binding.ivFood, item.urlImage)
            } else {
                binding.ivFood.setImageResource(R.drawable.ic_launcher_foreground)
            }
            binding.ivFavorite.setImageResource(
                if (item.hasLiked) R.drawable.ic_heart else R.drawable.ic_heart_outline
            )

            binding.root.setOnClickListener { onItemClick(item) }
            binding.ivFavorite.setOnClickListener { onFavoriteClick(item) }
            binding.btnAddToCart.setOnClickListener { onAddToCartClick(item) }
        }
    }

    private class FoodItemDiffUtil : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean =
            oldItem == newItem
    }
}
