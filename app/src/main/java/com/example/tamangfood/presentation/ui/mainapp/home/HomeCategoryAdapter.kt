package com.example.tamangfood.presentation.ui.mainapp.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.databinding.ItemHomeCategoryBinding
import com.example.tamangfood.domain.model.FoodCategory

class HomeCategoryAdapter : ListAdapter<FoodCategory, HomeCategoryAdapter.ViewHolder>(CategoryDiffUtilCallBack) {

    inner class ViewHolder(val binding: ItemHomeCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHomeCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.ivCategoryIcon.setImageResource(item.type.imageRes)
        holder.binding.tvCategoryName.text = item.name
    }

    private companion object CategoryDiffUtilCallBack : DiffUtil.ItemCallback<FoodCategory>() {
        override fun areItemsTheSame(oldItem: FoodCategory, newItem: FoodCategory) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: FoodCategory, newItem: FoodCategory) =
            oldItem == newItem
    }
}
