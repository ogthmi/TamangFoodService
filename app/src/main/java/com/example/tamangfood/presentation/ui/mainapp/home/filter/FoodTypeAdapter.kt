package com.example.tamangfood.presentation.ui.mainapp.home.filter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.databinding.ItemFoodTypeBinding
import com.example.tamangfood.presentation.utils.FoodType

class FoodTypeAdapter(
    private val onItemClick: (FoodType) -> Unit
) : ListAdapter<FoodType, FoodTypeAdapter.FoodTypeViewHolder>(FoodTypeDiffCallback()) {
    inner class FoodTypeViewHolder(val binding: ItemFoodTypeBinding) : RecyclerView.ViewHolder(binding.root)

    class FoodTypeDiffCallback : DiffUtil.ItemCallback<FoodType>() {
        override fun areItemsTheSame(oldItem: FoodType, newItem: FoodType): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: FoodType, newItem: FoodType): Boolean {
            return oldItem == newItem
        }
    }

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodTypeViewHolder {

        val binding = ItemFoodTypeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return FoodTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodTypeViewHolder, position: Int) {
        val foodType = getItem(position)

        val selected = position == selectedPosition

        val foodTypeImg = if (selected) foodType.tabSelector else foodType.imageRes

        holder.binding.tvFoodTypeName.apply {
            setText(foodType.title)
        }

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
}