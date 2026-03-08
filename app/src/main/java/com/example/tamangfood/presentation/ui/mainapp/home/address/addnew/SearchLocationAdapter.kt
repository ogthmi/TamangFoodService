package com.example.tamangfood.presentation.ui.mainapp.home.address.addnew

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.data.model.Address
import com.example.tamangfood.data.model.SearchLocationItem
import com.example.tamangfood.databinding.ItemSearchLocationBinding

class SearchLocationAdapter(
    private val onItemClick: (SearchLocationItem) -> Unit
) : ListAdapter<SearchLocationItem, SearchLocationAdapter.SearchLocationViewHolder>(SearchLocationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchLocationViewHolder {
        val binding = ItemSearchLocationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchLocationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchLocationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchLocationViewHolder(
        private val binding: ItemSearchLocationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchLocationItem) {
            binding.apply {
                tvLocationName.text = item.fullAddress
                root.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }

    class SearchLocationDiffCallback : DiffUtil.ItemCallback<SearchLocationItem>() {
        override fun areItemsTheSame(oldItem: SearchLocationItem, newItem: SearchLocationItem): Boolean {
            return oldItem.geoPoint.latitude == newItem.geoPoint.latitude &&
                   oldItem.geoPoint.longitude == newItem.geoPoint.longitude
        }

        override fun areContentsTheSame(oldItem: SearchLocationItem, newItem: SearchLocationItem): Boolean {
            return oldItem == newItem
        }
    }
}

