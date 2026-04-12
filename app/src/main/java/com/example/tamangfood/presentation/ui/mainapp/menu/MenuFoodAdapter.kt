package com.example.tamangfood.presentation.ui.mainapp.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.R
import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.databinding.ItemMenuFoodCardBinding
import com.example.tamangfood.presentation.utils.ImageLoader

class MenuFoodAdapter(
    private val onItemClick: (Food) -> Unit,
    private val onAddToCartClick: (Food) -> Unit
) : PagingDataAdapter<Food, MenuFoodAdapter.MenuFoodViewHolder>(FoodDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuFoodViewHolder {
        val binding = ItemMenuFoodCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuFoodViewHolder(binding, onItemClick, onAddToCartClick)
    }

    override fun onBindViewHolder(holder: MenuFoodViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.bind(item)
    }

    class MenuFoodViewHolder(
        private val binding: ItemMenuFoodCardBinding,
        private val onItemClick: (Food) -> Unit,
        private val onAddToCartClick: (Food) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Food) {
            binding.tvTitle.text = item.name
            binding.tvPrice.text = "$" + item.price.toString()
            binding.tvRating.text = item.avgRating.toString()
            binding.tvDescription.text = item.description ?: ""
            if (!item.urlImage.isNullOrBlank()) {
                ImageLoader.load(binding.root.context, binding.ivFood, item.urlImage)
            } else {
                binding.ivFood.setImageResource(R.drawable.ic_launcher_foreground)
            }
            if(item.hasLiked){
                binding.ivFavorite.setImageResource(R.drawable.ic_heart)
            }
            else binding.ivFavorite.setImageResource(R.drawable.ic_heart_outline)
            binding.root.setOnClickListener { onItemClick(item) }
            binding.btnAddToCart.setOnClickListener { onAddToCartClick(item) }
        }
    }

    private class FoodDiffUtil : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Food, newItem: Food) = oldItem == newItem
    }
}
