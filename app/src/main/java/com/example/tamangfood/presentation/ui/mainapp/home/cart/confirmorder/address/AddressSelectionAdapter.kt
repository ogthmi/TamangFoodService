package com.example.tamangfood.presentation.ui.mainapp.home.cart.confirmorder.address

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.domain.model.Address
import com.example.tamangfood.databinding.ItemAddressSelectionBinding

class AddressSelectionAdapter(
    private var selectedAddressId: Int,
    private val onItemClick: (Address) -> Unit
) : ListAdapter<Address, AddressSelectionAdapter.AddressViewHolder>(AddressDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressSelectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(getItem(position), selectedAddressId == getItem(position).id)
    }

    fun updateSelectedAddress(addressId: Int) {
        val oldSelected = selectedAddressId
        selectedAddressId = addressId
        notifyItemChanged(currentList.indexOfFirst { it.id == oldSelected })
        notifyItemChanged(currentList.indexOfFirst { it.id == addressId })
    }

    inner class AddressViewHolder(
        private val binding: ItemAddressSelectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(address: Address, isSelected: Boolean) {
            binding.apply {
                tvAddressName.text = address.name
                tvAddressFull.text = address.fullAddress

                // Show/hide selection indicator
                ivSelected.visibility = if (isSelected) View.VISIBLE else View.GONE

                root.setOnClickListener {
                    updateSelectedAddress(address.id)
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