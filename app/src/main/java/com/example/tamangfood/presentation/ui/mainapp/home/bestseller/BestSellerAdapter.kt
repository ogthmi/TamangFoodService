package com.example.tamangfood.presentation.ui.mainapp.home.bestseller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.R
import com.example.tamangfood.data.model.Food
import com.example.tamangfood.databinding.ItemBestSellerGridBinding
import com.example.tamangfood.presentation.utils.FoodType
import com.example.tamangfood.presentation.utils.ImageFoodType
import com.example.tamangfood.presentation.utils.Utils

class BestSellerAdapter(
    private val onItemClick: (Food) -> Unit,
    private val onFavoriteClick: (Food) -> Unit,
    private val onAddToCartClick: (Food) -> Unit,
) : ListAdapter<Food, BestSellerAdapter.BestSellerViewHolder>(BestSellerDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestSellerViewHolder {
        val binding = ItemBestSellerGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BestSellerViewHolder(binding, onItemClick, onFavoriteClick, onAddToCartClick)
    }

    override fun onBindViewHolder(holder: BestSellerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BestSellerViewHolder(
        private val binding: ItemBestSellerGridBinding,
        private val onItemClick: (Food) -> Unit,
        private val onFavoriteClick: (Food) -> Unit,
        private val onAddToCartClick: (Food) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Food) {
            binding.tvTitle.text = item.name
            binding.tvSubtitle.text = item.description
            binding.tvPrice.text = item.price
            binding.tvRating.text = item.rating.toString()
            binding.ivFood.setImageResource(item.imageRes)
            binding.ivCategory.setImageResource(ImageFoodType.valueOf(item.type.name).imageRes)

            binding.root.setOnClickListener { onItemClick(item) }
            binding.ivFavorite.setOnClickListener { onFavoriteClick(item) }
            binding.btnAddToCart.setOnClickListener { onAddToCartClick(item) }
        }
    }

    private class BestSellerDiffUtil : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean =
            oldItem == newItem
    }
}

