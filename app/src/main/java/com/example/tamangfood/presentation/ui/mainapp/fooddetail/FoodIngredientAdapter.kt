package com.example.tamangfood.presentation.ui.mainapp.fooddetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.databinding.ItemFoodIngredientBinding

class FoodIngredientAdapter(
    private val onItemClick: (FoodIngredient) -> Unit
) : ListAdapter<FoodIngredient, FoodIngredientAdapter.FoodIngredientViewHolder>(
    FoodIngredientDiffCallback()
) {
    inner class FoodIngredientViewHolder(val binding: ItemFoodIngredientBinding) :
        RecyclerView.ViewHolder(binding.root)

    class FoodIngredientDiffCallback : DiffUtil.ItemCallback<FoodIngredient>() {
        override fun areItemsTheSame(oldItem: FoodIngredient, newItem: FoodIngredient): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FoodIngredient, newItem: FoodIngredient): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodIngredientViewHolder {
        val binding = ItemFoodIngredientBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return FoodIngredientViewHolder(binding)
    }

    private var selectedIngredients = mutableSetOf<String>()

    override fun onBindViewHolder(holder: FoodIngredientViewHolder, position: Int) {
        val context = holder.itemView.context

        val ingredient = getItem(position)

        val selected = selectedIngredients.contains(ingredient.id)

        holder.binding.apply {
            tvIngredientName.text = ingredient.name

            val priceDouble = ingredient.price
            tvPrice.text = String.format("$%05.2f", priceDouble)

            cbIngredientSelected.apply {
                setOnCheckedChangeListener(null)
                isChecked = selected

                setOnCheckedChangeListener { _, checked ->
                    if (checked) selectedIngredients.add(ingredient.id)
                    else selectedIngredients.remove(ingredient.id)

                    onItemClick(ingredient)
                }
            }

            root.setOnClickListener {
                cbIngredientSelected.isChecked = !cbIngredientSelected.isChecked
            }

        }
    }

    fun getSelectedIngredients(): List<FoodIngredient> {
        return currentList.filter { selectedIngredients.contains(it.id) }
    }
}