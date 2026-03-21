package com.example.tamangfood.presentation.ui.mainapp.home.cart.addtocart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.data.model.Food
import com.example.tamangfood.databinding.BottomSheetAddToCartBinding
import com.example.tamangfood.presentation.ui.mainapp.fooddetail.FoodIngredient
import com.example.tamangfood.presentation.ui.mainapp.fooddetail.FoodIngredientAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddToCartBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetAddToCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var foodIngredientAdapter: FoodIngredientAdapter
    private lateinit var food: Food

    private var orderQuantity = 1
    private val mockFoodIngredients = listOf(
        FoodIngredient("1", "Hot sauce", 1.0),
        FoodIngredient("2", "Ketchup", 10.0),
        FoodIngredient("3", "Mustard", 1.0),
        FoodIngredient("4", "Mustard", 10.0),
        FoodIngredient("5", "Mustard", 1.0),
        FoodIngredient("6", "Mustard", 10.0),
        FoodIngredient("7", "Mustard", 1.0),
        FoodIngredient("8", "Mustard", 10.0),
        FoodIngredient("9", "Mustard", 1.0),
        FoodIngredient("10", "Mustard", 1.50),
        FoodIngredient("11", "Mustard", 1.0),
        FoodIngredient("12", "Mustard", 10.0),
        FoodIngredient("13", "Mustard", 1.0),
        FoodIngredient("14", "Mustard", 10.0),
        FoodIngredient("15", "Mustard", 10.0),
        FoodIngredient("16", "Mustard", 1.0),
        FoodIngredient("17", "Mustard", 10.0),
    )

    companion object {
        const val TAG = "AddToCardBottomSheet"
        const val ARG_FOOD = "arg_food"

        fun newInstance(food: Food): AddToCartBottomSheet {
            return AddToCartBottomSheet().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_FOOD, food)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetAddToCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            food = it.getParcelable(ARG_FOOD)!!
        }
        setupFoodInfo()
        setupQuantity()
        setIngredientRecyclerView()
        setClickListeners()
    }

    override fun onStart() {
        super.onStart()

        dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.let { bottomSheet ->
            bottomSheet.setBackgroundResource(android.R.color.transparent)

            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.isFitToContents = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }

    private fun setupFoodInfo() {
        binding.apply {
            ivFoodImage.setImageResource(food.imageRes)

            tvFoodName.text = food.name
            tvFoodDescriptionReduced.text = food.description
            tvFoodPrice.text = food.price

            tvAvailableQuantity.text = getString(R.string.available_quantity, food.quantity)
        }
    }

    private fun setupQuantity() {
        binding.etQuantity.setText(orderQuantity.toString())

        fun updateQuantitySelector() {
            val canDecrease = orderQuantity > 1
            val canIncrease = orderQuantity < food.quantity

            binding.ivDecrease.isEnabled = canDecrease
            binding.ivDecrease.setImageResource(
                if (canDecrease) R.drawable.ic_minus_active_red
                else R.drawable.ic_minus_unactive
            )

            binding.ivIncrease.isEnabled = canIncrease
            binding.ivIncrease.setImageResource(
                if (canIncrease) R.drawable.ic_plus_active_red
                else R.drawable.ic_plus_unactive
            )

            binding.etQuantity.apply {
                if (text.toString() != orderQuantity.toString()) setText(orderQuantity.toString())
            }
        }
        updateQuantitySelector()

        binding.ivDecrease.setOnClickListener {
            if (orderQuantity > 1) {
                orderQuantity--
                updateQuantitySelector()
            }
        }

        binding.ivIncrease.setOnClickListener {
            if (orderQuantity < food.quantity) {
                orderQuantity++
                updateQuantitySelector()
            }
        }

        binding.etQuantity.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val input = binding.etQuantity.text.toString().toIntOrNull() ?: 1
                orderQuantity = input.coerceIn(1, food.quantity)
                updateQuantitySelector()
            }
        }
    }

    private fun setIngredientRecyclerView(){
        foodIngredientAdapter = FoodIngredientAdapter(
            onItemClick = { foodIngredient ->
                //TODO: handle click food ingredient
            }
        )
        binding.rvIngredients.apply {
            adapter = foodIngredientAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            itemAnimator = null
        }
        foodIngredientAdapter.submitList(mockFoodIngredients)
    }

    private fun setClickListeners() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}