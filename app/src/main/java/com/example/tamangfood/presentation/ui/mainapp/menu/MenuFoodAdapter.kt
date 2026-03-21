package com.example.tamangfood.presentation.ui.mainapp.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.data.model.Food
import com.example.tamangfood.databinding.ItemMenuFoodCardBinding

class MenuFoodAdapter(
    private val onItemClick: (Food) -> Unit,
    private val onAddToCartClick: (Food) -> Unit
) : ListAdapter<Food, MenuFoodAdapter.MenuFoodViewHolder>(Diff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuFoodViewHolder {
        val binding = ItemMenuFoodCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuFoodViewHolder(binding, onItemClick, onAddToCartClick)
    }

    override fun onBindViewHolder(holder: MenuFoodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MenuFoodViewHolder(
        private val binding: ItemMenuFoodCardBinding,
        private val onItemClick: (Food) -> Unit,
        private val onAddToCartClick: (Food) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Food) {
            binding.tvTitle.text = item.name
            binding.tvPrice.text = item.price
            binding.tvRating.text = item.rating.toString()
            binding.tvDescription.text = item.description ?: ""
            binding.ivFood.setImageResource(item.imageRes)
            binding.root.setOnClickListener { onItemClick(item) }
            binding.btnAddToCart.setOnClickListener { onAddToCartClick(item) }
        }
    }

    private class Diff : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Food, newItem: Food) = oldItem == newItem
    }
}
