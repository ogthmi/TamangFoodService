package com.example.tamangfood.presentation.ui.mainapp.fooddetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.R
import com.example.tamangfood.databinding.ItemFoodReviewBinding
import com.example.tamangfood.domain.model.FoodComment
import com.example.tamangfood.presentation.utils.ImageLoader

class FoodReviewAdapter : ListAdapter<FoodComment, FoodReviewAdapter.FoodReviewViewHolder>(
    FoodReviewDiffCallback()
) {
    inner class FoodReviewViewHolder(val binding: ItemFoodReviewBinding) :
        RecyclerView.ViewHolder(binding.root)

    class FoodReviewDiffCallback : DiffUtil.ItemCallback<FoodComment>() {
        override fun areItemsTheSame(oldItem: FoodComment, newItem: FoodComment) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: FoodComment, newItem: FoodComment) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodReviewViewHolder {
        val binding = ItemFoodReviewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FoodReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodReviewViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            tvFullName.text = item.fullName
            tvReview.text = item.text
            rbRating.rating = item.rating.toFloat().coerceIn(0f, 5f)
            val url = item.avatarUrl
            if (!url.isNullOrBlank()) {
                ivAvatar.isVisible = true
                ImageLoader.load(root.context, ivAvatar, url)
            } else {
                ivAvatar.isVisible = true
                ivAvatar.setImageResource(R.drawable.ic_default_avatar)
            }
        }
    }
}
