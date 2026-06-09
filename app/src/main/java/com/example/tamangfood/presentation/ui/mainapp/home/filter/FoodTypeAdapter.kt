package com.example.tamangfood.presentation.ui.mainapp.home.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.databinding.ItemFoodTypeBinding
import com.example.tamangfood.domain.model.FoodCategory
class FoodTypeAdapter(
    private val onItemClick: (FoodCategory) -> Unit
) : ListAdapter<FoodCategory, FoodTypeAdapter.FoodTypeViewHolder>(Diff) {

    inner class FoodTypeViewHolder(val binding: ItemFoodTypeBinding) :
        RecyclerView.ViewHolder(binding.root)

    private var selectedPosition = 0

    fun resetSelectionToFirst() {
        if (currentList.isEmpty()) return
        val old = selectedPosition.coerceAtMost(currentList.lastIndex)
        selectedPosition = 0
        notifyItemChanged(old)
        notifyItemChanged(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodTypeViewHolder {
        val binding = ItemFoodTypeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FoodTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodTypeViewHolder, position: Int) {
        val category = getItem(position)
        val selected = position == selectedPosition
        val foodTypeImg =
            if (selected) category.type.tabSelector else category.type.imageRes

        holder.binding.tvFoodTypeName.text = category.name

        holder.binding.btnFoodType.apply {
            setImageResource(foodTypeImg)
            setOnClickListener {
                val pos = holder.bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return@setOnClickListener
                val old = selectedPosition
                selectedPosition = pos
                notifyItemChanged(old)
                notifyItemChanged(pos)
                onItemClick(getItem(pos))
            }
            isSelected = selected
        }
    }

    private companion object Diff : DiffUtil.ItemCallback<FoodCategory>() {
        override fun areItemsTheSame(oldItem: FoodCategory, newItem: FoodCategory) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: FoodCategory, newItem: FoodCategory) =
            oldItem == newItem
    }
}
