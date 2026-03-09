package com.example.tamangfood.presentation.ui.mainapp.order.cancel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.R
import com.example.tamangfood.data.model.CancelReason
import com.example.tamangfood.databinding.ItemCancelReasonBinding

class CancelReasonAdapter(
    private val onReasonSelected: (CancelReason) -> Unit
) : ListAdapter<CancelReason, CancelReasonAdapter.CancelReasonViewHolder>(
    CancelReasonDiffCallback()
) {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CancelReasonViewHolder {
        val binding = ItemCancelReasonBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CancelReasonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CancelReasonViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    inner class CancelReasonViewHolder(
        private val binding: ItemCancelReasonBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reason: CancelReason, isSelected: Boolean) {
            binding.apply {
                tvReasonText.text = reason.text
                ivSelected.visibility = if (isSelected) ViewGroup.VISIBLE else ViewGroup.GONE
                
                // Change background color when selected
                if (isSelected) {
                    root.setBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.unselected)
                    )
                } else {
                    root.background = null
                }

                root.setOnClickListener {
                    val previousPosition = selectedPosition
                    selectedPosition = adapterPosition
                    
                    if (previousPosition != -1) {
                        notifyItemChanged(previousPosition)
                    }
                    notifyItemChanged(selectedPosition)
                    onReasonSelected(reason)
                }
            }
        }
    }

    class CancelReasonDiffCallback : DiffUtil.ItemCallback<CancelReason>() {
        override fun areItemsTheSame(oldItem: CancelReason, newItem: CancelReason): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CancelReason, newItem: CancelReason): Boolean {
            return oldItem == newItem
        }
    }

    fun getSelectedReason(): CancelReason? {
        return if (selectedPosition != -1 && selectedPosition < itemCount) {
            getItem(selectedPosition)
        } else {
            null
        }
    }
}
