package com.example.tamangfood.presentation.ui.mainapp.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.R
import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.databinding.ItemOrderProductBinding
import com.example.tamangfood.presentation.utils.ImageLoader

class OrderItemAdapter(
    private val onItemClick: (Food) -> Unit,
    private val showItemActions: Boolean = false,
    private val onOrderAgainClick: ((Food) -> Unit)? = null,
    private val onLeaveCommentClick: ((Food) -> Unit)? = null
) : ListAdapter<Food, OrderItemAdapter.OrderItemViewHolder>(
    OrderItemDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = ItemOrderProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderItemViewHolder(
            binding = binding,
            onItemClick = onItemClick,
            showItemActions = showItemActions,
            onOrderAgainClick = onOrderAgainClick,
            onLeaveCommentClick = onLeaveCommentClick
        )
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderItemViewHolder(
        private val binding: ItemOrderProductBinding,
        private val onItemClick: (Food) -> Unit,
        private val showItemActions: Boolean,
        private val onOrderAgainClick: ((Food) -> Unit)?,
        private val onLeaveCommentClick: ((Food) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Food) {
            binding.apply {
                tvProductName.text = item.name
                tvProductPrice.text = String.format("$%d", item.price)
                tvProductQuantity.text = "x${item.quantity}"
                val ingredientText = item.ingredientResponse.name.trim()
                if (ingredientText.isNotBlank()) {
                    tvProductIngredients.visibility = View.VISIBLE
                    tvProductIngredients.text = ingredientText
                } else {
                    tvProductIngredients.visibility = View.GONE
                }
                if (!item.urlImage.isNullOrBlank()) {
                    ImageLoader.load(binding.root.context, binding.ivProductImage, item.urlImage)
                } else {
                    binding.ivProductImage.setImageResource(R.drawable.ic_launcher_foreground)
                }
                layoutItemActions.visibility = if (showItemActions) View.VISIBLE else View.GONE
                btnItemOrderAgain.setOnClickListener { onOrderAgainClick?.invoke(item) }
                btnItemLeaveComment.setOnClickListener { onLeaveCommentClick?.invoke(item) }

                root.setOnClickListener { onItemClick(item) }
            }
        }
    }

    class OrderItemDiffCallback : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem == newItem
        }
    }
}
