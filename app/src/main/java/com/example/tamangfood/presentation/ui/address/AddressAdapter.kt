package com.example.tamangfood.presentation.ui.address

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.data.model.Address
import com.example.tamangfood.databinding.ItemAddressBinding

class AddressAdapter(
    private val onItemClick: (Address) -> Unit
) : ListAdapter<Address, AddressAdapter.AddressViewHolder>(AddressDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AddressViewHolder(
        private val binding: ItemAddressBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(address: Address) {
            binding.apply {
                tvAddressName.text = address.name
                tvAddressFull.text = address.fullAddress

                root.setOnClickListener {
                    onItemClick(address)
                }
            }
        }
    }

    class AddressDiffCallback : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }
}

