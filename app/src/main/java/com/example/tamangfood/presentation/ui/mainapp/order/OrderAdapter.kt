package com.example.tamangfood.presentation.ui.mainapp.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.domain.model.Order
import com.example.tamangfood.databinding.ItemOrderActiveBinding
import com.example.tamangfood.databinding.ItemOrderCancelledBinding
import com.example.tamangfood.databinding.ItemOrderCompletedBinding
import com.example.tamangfood.presentation.utils.OrderStatus

class OrderAdapter(
    private val onCancelClick: (Order) -> Unit,
    private val onTrackClick: (Order) -> Unit,
    private val onReviewClick: (Order) -> Unit,
    private val onOrderAgainClick: (Order) -> Unit,
    private val onItemClick: (Order) -> Unit,
    private val onFoodClick: (Food) -> Unit
) : ListAdapter<Order, RecyclerView.ViewHolder>(OrderDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).status) {
            OrderStatus.ACTIVE -> VIEW_TYPE_ACTIVE
            OrderStatus.COMPLETED -> VIEW_TYPE_COMPLETED
            OrderStatus.CANCELLED -> VIEW_TYPE_CANCELLED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ACTIVE -> {
                val binding = ItemOrderActiveBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ActiveOrderViewHolder(binding, onCancelClick, onTrackClick, onItemClick, onFoodClick)
            }

            VIEW_TYPE_COMPLETED -> {
                val binding = ItemOrderCompletedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CompletedOrderViewHolder(binding, onReviewClick, onOrderAgainClick, onItemClick, onFoodClick)
            }

            VIEW_TYPE_CANCELLED -> {
                val binding = ItemOrderCancelledBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CancelledOrderViewHolder(binding, onItemClick, onFoodClick)
            }

            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val order = getItem(position)) {
            is Order -> {
                when (holder) {
                    is ActiveOrderViewHolder -> holder.bind(order)
                    is CompletedOrderViewHolder -> holder.bind(order)
                    is CancelledOrderViewHolder -> holder.bind(order)
                }
            }
        }
    }

    class ActiveOrderViewHolder(
        private val binding: ItemOrderActiveBinding,
        private val onCancelClick: (Order) -> Unit,
        private val onTrackClick: (Order) -> Unit,
        private val onItemClick: (Order) -> Unit,
        private val onFoodClick: (Food) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private val orderItemAdapter = OrderItemAdapter(onItemClick = onFoodClick)

        init {
            binding.rvOrderItems.apply {
                layoutManager = LinearLayoutManager(binding.root.context)
                adapter = orderItemAdapter
            }
        }

        fun bind(order: Order) {
            binding.apply {
                tvOrderName.text = order.name
                tvOrderPrice.text = "$" + order.price
                tvOrderDate.text = order.dateTime
                tvItemCount.text = "${order.itemCount} items"
                tvTotalPrice.text = "Total price: $${order.price - order.deliveryTax}"
                tvDeliveryTax.text = "Delivery tax: $${order.deliveryTax}"

                // Bind order items
                orderItemAdapter.submitList(order.items)
                btnCancelOrder.setOnClickListener { onCancelClick(order) }
                btnTrackDriver.setOnClickListener { onTrackClick(order) }
                itemView.setOnClickListener { onItemClick(order) }
            }
        }
    }

    class CompletedOrderViewHolder(
        private val binding: ItemOrderCompletedBinding,
        private val onReviewClickForOrder: (Order) -> Unit,
        private val onOrderAgainForOrder: (Order) -> Unit,
        private val onItemClick: (Order) -> Unit,
        private val onFoodClick: (Food) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private val orderItemAdapter = OrderItemAdapter(
            onItemClick = onFoodClick,
            showItemActions = true,
            onOrderAgainClick = { orderInScope?.let { onOrderAgainForOrder(it) } },
            onLeaveCommentClick = { orderInScope?.let { onReviewClickForOrder(it) } }
        )
        private var orderInScope: Order? = null

        init {
            binding.rvOrderItems.apply {
                layoutManager = LinearLayoutManager(binding.root.context)
                adapter = orderItemAdapter
            }
        }

        fun bind(order: Order) {
            orderInScope = order
            binding.apply {
                val totalPrice = order.price - order.deliveryTax
                tvOrderName.text = order.name
                tvOrderPrice.text = "$" + order.price
                tvOrderDate.text = order.dateTime
                tvItemCount.text = "${order.itemCount} items"
                tvTotalPrice.text = "Total price: $${order.price - order.deliveryTax}"
                tvDeliveryTax.text = "Delivery tax: $${order.deliveryTax}"

                orderItemAdapter.submitList(order.items)
                itemView.setOnClickListener { onItemClick(order) }
            }
        }
    }

    class CancelledOrderViewHolder(
        private val binding: ItemOrderCancelledBinding,
        private val onItemClick: (Order) -> Unit,
        private val onFoodClick: (Food) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private val orderItemAdapter = OrderItemAdapter(onItemClick = onFoodClick)

        init {
            binding.rvOrderItems.apply {
                layoutManager = LinearLayoutManager(binding.root.context)
                adapter = orderItemAdapter
            }
        }

        fun bind(order: Order) {
            binding.apply {
                tvOrderName.text = order.name
                tvOrderPrice.text = "$" + order.price
                tvOrderDate.text = order.dateTime
                tvItemCount.text = "${order.itemCount} items"
                tvOrderStatus.text = order.statusText

                // Bind order items
                orderItemAdapter.submitList(order.items)
                itemView.setOnClickListener { onItemClick(order) }
            }
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val VIEW_TYPE_ACTIVE = 1
        private const val VIEW_TYPE_COMPLETED = 2
        private const val VIEW_TYPE_CANCELLED = 3
    }
}
