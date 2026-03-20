package com.example.tamangfood.presentation.ui.mainapp.fooddetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.databinding.ItemFoodReviewBinding

class FoodReviewAdapter: ListAdapter<FoodReview, FoodReviewAdapter.FoodReviewViewHolder>(
    FoodReviewDiffCallback()
) {
    inner class FoodReviewViewHolder(val binding: ItemFoodReviewBinding) :
        RecyclerView.ViewHolder(binding.root)

    class FoodReviewDiffCallback : DiffUtil.ItemCallback<FoodReview>() {
        override fun areItemsTheSame(oldItem: FoodReview, newItem: FoodReview): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FoodReview, newItem: FoodReview): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): FoodReviewViewHolder {
        val binding = ItemFoodReviewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return FoodReviewViewHolder(binding)
    }

    override fun onBindViewHolder( holder: FoodReviewViewHolder, position: Int) {
        val foodReview = getItem(position)

        holder.binding.apply {
            tvFullName.text = foodReview.fullName
            tvReview.text = foodReview.review

            rbRating.rating = foodReview.rating.toFloat()
        }
    }
}