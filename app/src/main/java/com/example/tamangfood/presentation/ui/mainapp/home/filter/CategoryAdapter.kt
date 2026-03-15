package com.example.tamangfood.presentation.ui.mainapp.home.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.R
import com.example.tamangfood.databinding.ItemFoodCategoryBinding

class CategoryAdapter(
    private val onItemClick: (String) -> Unit
) : ListAdapter<String, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {
    inner class CategoryViewHolder(val binding: ItemFoodCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    class CategoryDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    private val selectedPositions = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemFoodCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val context = holder.itemView.context

        val category = getItem(position)

        val selected = selectedPositions.contains(position)

        val textColor = if (selected) R.color.white else R.color.orange_base

        holder.binding.tvCategoryName.apply {
            text = category
            isSelected = selected

            setTextColor( ContextCompat.getColor(context, textColor))

            setOnClickListener {
                val pos = holder.bindingAdapterPosition

                if (!selectedPositions.add(pos)) selectedPositions.remove(pos)

                notifyItemChanged(pos)

                onItemClick(category)
            }
        }
    }

    fun clearSelection() {
        selectedPositions.clear()
    }
}