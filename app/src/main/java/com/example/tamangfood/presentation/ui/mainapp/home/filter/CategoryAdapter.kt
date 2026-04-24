package com.example.tamangfood.presentation.ui.mainapp.home.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.R
import com.example.tamangfood.databinding.ItemFoodCategoryBinding
import com.example.tamangfood.domain.model.FoodCategoryDetail

class CategoryAdapter(
    private val onItemClick: (FoodCategoryDetail) -> Unit
) : ListAdapter<FoodCategoryDetail, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {
    inner class CategoryViewHolder(val binding: ItemFoodCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    class CategoryDiffCallback : DiffUtil.ItemCallback<FoodCategoryDetail>() {
        override fun areItemsTheSame(oldItem: FoodCategoryDetail, newItem: FoodCategoryDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: FoodCategoryDetail,
            newItem: FoodCategoryDetail
        ): Boolean {
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

        val item = getItem(position)

        val selected = selectedPositions.contains(position)

        val textColor = if (selected) R.color.white else R.color.orange_base

        holder.binding.tvCategoryName.apply {
            text = item.name
            isSelected = selected

            setTextColor(ContextCompat.getColor(context, textColor))

            setOnClickListener {
                val pos = holder.bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener

                if (!selectedPositions.add(pos)) selectedPositions.remove(pos)

                notifyItemChanged(pos)

                onItemClick(item)
            }
        }
    }

    fun clearSelection() {
        selectedPositions.clear()
    }

    fun getSelectedItems(): List<FoodCategoryDetail> {
        return selectedPositions.mapNotNull { pos ->
            if (pos < currentList.size) currentList[pos] else null
        }
    }
}
