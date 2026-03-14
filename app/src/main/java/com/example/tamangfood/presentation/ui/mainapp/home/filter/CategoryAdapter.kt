package com.example.tamangfood.presentation.ui.mainapp.home.filter

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.R
import com.example.tamangfood.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val items: List<Category>,
    private val onClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var selectedPosition = 0

    inner class ViewHolder(val binding: ItemCategoryBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = items[position]

        holder.binding.tvCategoryName.text = item.name
        holder.binding.btnCategory.setImageResource(item.image)

        holder.binding.btnCategory.setOnClickListener {

            val oldPosition = selectedPosition
            selectedPosition = position

            notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)

            onClick(item)
        }
    }
}