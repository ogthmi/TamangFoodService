package com.example.tamangfood.presentation.ui.mainapp.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.R
import com.example.tamangfood.databinding.ItemFoodBinding
import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.presentation.utils.ImageLoader

class FoodPagingAdapter(
    private val onItemClick: (Food) -> Unit,
    private val onFavoriteClick: (Food) -> Unit,
    private val onAddToCartClick: (Food) -> Unit
) : PagingDataAdapter<Food, FoodPagingAdapter.FoodViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class FoodViewHolder(
        private val binding: ItemFoodBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(food: Food) {
            binding.apply {
                tvTitle.text = food.name
                tvSubtitle.text = food.description ?: ""
                tvPrice.text = "$${food.price}"
                tvRating.text = food.avgRating.toString()

                if (!food.urlImage.isNullOrEmpty()) {
                    ImageLoader.load(root.context, ivFood, food.urlImage)
                } else {
                    ivFood.setImageResource(R.drawable.ic_launcher_background)
                }

                ivFavorite.setImageResource(
                    if (food.hasLiked)
                        R.drawable.ic_heart
                    else
                        R.drawable.ic_heart_outline
                )

                root.setOnClickListener {
                    onItemClick(food)
                }

                ivFavorite.setOnClickListener {
                    onFavoriteClick(food)
                }

                btnAddToCart.setOnClickListener {
                    onAddToCartClick(food)
                }
            }
        }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Food>() {
            override fun areItemsTheSame(old: Food, new: Food) = old.id == new.id
            override fun areContentsTheSame(old: Food, new: Food) = old == new
        }
    }
}