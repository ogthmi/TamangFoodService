package com.example.tamangfood.presentation.ui.mainapp.home.cart.addtocart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.databinding.BottomSheetAddToCartBinding
import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.domain.model.FoodDetail
import com.example.tamangfood.domain.model.Ingredient
import com.example.tamangfood.presentation.ui.mainapp.fooddetail.FoodIngredient
import com.example.tamangfood.presentation.ui.mainapp.fooddetail.FoodIngredientAdapter
import com.example.tamangfood.presentation.utils.ImageLoader
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddToCartBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetAddToCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddToCartBottomSheetViewModel by viewModels()

    private lateinit var foodIngredientAdapter: FoodIngredientAdapter
    private lateinit var food: Food

    private var orderQuantity = 1

    companion object {
        const val TAG = "AddToCartBottomSheet"
        private const val ARG_FOOD_ID = "arg_food_id"
        private const val ARG_FOOD_NAME = "arg_food_name"
        private const val ARG_FOOD_DESCRIPTION = "arg_food_description"
        private const val ARG_FOOD_PRICE = "arg_food_price"
        private const val ARG_FOOD_QUANTITY = "arg_food_quantity"
        private const val ARG_FOOD_URL_IMAGE = "arg_food_url_image"
        private const val ARG_INGREDIENT_ID = "arg_ingredient_id"
        private const val ARG_INGREDIENT_NAME = "arg_ingredient_name"
        private const val ARG_INGREDIENT_PRICE = "arg_ingredient_price"

        fun newInstance(food: Food): AddToCartBottomSheet {
            return AddToCartBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt(ARG_FOOD_ID, food.id)
                    putString(ARG_FOOD_NAME, food.name)
                    putString(ARG_FOOD_DESCRIPTION, food.description.orEmpty())
                    putInt(ARG_FOOD_PRICE, food.price)
                    putInt(ARG_FOOD_QUANTITY, food.quantity)
                    putString(ARG_FOOD_URL_IMAGE, food.urlImage)
                    putInt(ARG_INGREDIENT_ID, food.ingredientResponse.id)
                    putString(ARG_INGREDIENT_NAME, food.ingredientResponse.name)
                    putInt(ARG_INGREDIENT_PRICE, food.ingredientResponse.price)
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
        food = buildFoodFromArguments()
        setupFoodInfo()
        setupQuantity()
        setIngredientRecyclerView()
        setClickListeners()
        observeAddToCart()
        observeFoodDetail()
        viewModel.loadFoodDetail(food.id)
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
            if (!food.urlImage.isNullOrBlank()) {
                ImageLoader.load(requireContext(), ivFoodImage, food.urlImage)
            } else {
                ivFoodImage.setImageResource(R.drawable.ic_launcher_foreground)
            }

            tvFoodName.text = food.name
            tvFoodDescriptionReduced.text = food.description.orEmpty()
            tvFoodPrice.text = String.format("$%d", food.price)

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

    private fun setIngredientRecyclerView() {
        foodIngredientAdapter = FoodIngredientAdapter(
            onItemClick = { }
        )
        binding.rvIngredients.apply {
            adapter = foodIngredientAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            itemAnimator = null
        }

        val ingredients = buildFallbackIngredientList(food.ingredientResponse)
        foodIngredientAdapter.submitList(ingredients)
        binding.tvAddIngredient.isVisible = ingredients.isNotEmpty()
        binding.rvIngredients.isVisible = ingredients.isNotEmpty()
    }

    private fun setClickListeners() {
        binding.btnClose.setOnClickListener {
            dismiss()
        }

        binding.btnAddToCart.setOnClickListener {
            if (food.quantity <= 0 || orderQuantity <= 0) {
                Utils.showToast(requireContext(), getString(R.string.food_detail_out_of_stock))
                return@setOnClickListener
            }

            val selectedIngredientIds = foodIngredientAdapter
                .getSelectedIngredients()
                .mapNotNull { it.id.toIntOrNull() }
                .distinct()

            viewModel.addToCart(
                foodId = food.id,
                quantity = orderQuantity,
                ingredientIds = selectedIngredientIds
            )
        }
    }

    private fun observeAddToCart() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addToCartState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> {
                            binding.btnAddToCart.isEnabled = false
                        }
                        is NetworkState.Success<*> -> {
                            binding.btnAddToCart.isEnabled = true
                            Utils.showToast(requireContext(), "Add to cart successful!")
                            viewModel.resetAddToCartState()
                            dismiss()
                        }
                        is NetworkState.Error -> {
                            binding.btnAddToCart.isEnabled = true
                            Utils.showToast(requireContext(), state.message)
                            viewModel.resetAddToCartState()
                        }
                    }
                }
            }
        }
    }

    private fun observeFoodDetail() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.foodDetailState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> Unit
                        is NetworkState.Success<*> -> {
                            @Suppress("UNCHECKED_CAST")
                            val detail = state.data as? FoodDetail
                            if (detail != null) {
                                val ingredients = detail.ingredients.map {
                                    FoodIngredient(
                                        id = it.id.toString(),
                                        name = it.name,
                                        price = it.price.toDouble()
                                    )
                                }
                                foodIngredientAdapter.submitList(ingredients)
                                binding.tvAddIngredient.isVisible = ingredients.isNotEmpty()
                                binding.rvIngredients.isVisible = ingredients.isNotEmpty()
                            }
                            viewModel.resetFoodDetailState()
                        }
                        is NetworkState.Error -> {
                            viewModel.resetFoodDetailState()
                        }
                    }
                }
            }
        }
    }

    private fun buildFallbackIngredientList(ingredient: Ingredient): List<FoodIngredient> {
        if (ingredient.id <= 0 || ingredient.name.isBlank()) return emptyList()
        return listOf(
            FoodIngredient(
                id = ingredient.id.toString(),
                name = ingredient.name,
                price = ingredient.price.toDouble()
            )
        )
    }

    private fun buildFoodFromArguments(): Food {
        val bundle = requireArguments()
        return Food(
            id = bundle.getInt(ARG_FOOD_ID),
            name = bundle.getString(ARG_FOOD_NAME).orEmpty(),
            urlImage = bundle.getString(ARG_FOOD_URL_IMAGE),
            description = bundle.getString(ARG_FOOD_DESCRIPTION).orEmpty(),
            price = bundle.getInt(ARG_FOOD_PRICE),
            avgRating = 0.0,
            totalComment = 0,
            totalLikes = 0,
            hasLiked = false,
            quantity = bundle.getInt(ARG_FOOD_QUANTITY),
            ingredientResponse = Ingredient(
                id = bundle.getInt(ARG_INGREDIENT_ID),
                name = bundle.getString(ARG_INGREDIENT_NAME).orEmpty(),
                price = bundle.getInt(ARG_INGREDIENT_PRICE)
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.resetFoodDetailState()
        _binding = null
    }
}