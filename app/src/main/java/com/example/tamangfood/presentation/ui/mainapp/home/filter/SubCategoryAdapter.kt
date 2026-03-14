package com.example.tamangfood.presentation.ui.mainapp.home.filter

import android.R.attr.onClick
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.R
import com.example.tamangfood.databinding.ItemSubCategoryBinding

class SubCategoryAdapter(
    private var items: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<SubCategoryAdapter.SubViewHolder>() {

    inner class SubViewHolder(
        val binding: ItemSubCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root)

    private val selectedPositions = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubViewHolder {
        val binding = ItemSubCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SubViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: SubViewHolder, position: Int) {

        val context = holder.itemView.context
        val subCategory = items[position]

        holder.binding.tvSubCategoryName.text = subCategory

        val isSelected = selectedPositions.contains(position)
        holder.binding.tvSubCategoryName.isSelected = isSelected

        holder.binding.tvSubCategoryName.setTextColor(
            ContextCompat.getColor(
                context,
                if (isSelected) R.color.white else R.color.orange_base
            )
        )

        holder.binding.tvSubCategoryName.setOnClickListener {

            if (selectedPositions.contains(position)) {
                selectedPositions.remove(position)
            } else {
                selectedPositions.add(position)
            }

            notifyItemChanged(position)

            onClick(subCategory)
        }
    }

    fun updateData(newItems: List<String>) {
        items = newItems
        selectedPositions.clear()

        notifyDataSetChanged()
    }
}