package com.example.tamangfood.presentation.ui.mainapp.fooddetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.data.model.Food
import com.example.tamangfood.databinding.FragmentFoodDetailBinding
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodDetailFragment : Fragment(R.layout.fragment_food_detail) {
    private var _binding: FragmentFoodDetailBinding? = null
    private val binding get() = _binding!!

    private val args: FoodDetailFragmentArgs by navArgs()
    private var orderQuantity: Int = 1
    private var isFavorite = false

    private lateinit var foodIngredientAdapter: FoodIngredientAdapter
    private lateinit var foodReviewAdapter: FoodReviewAdapter
    private lateinit var food: Food

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodDetailBinding.inflate(inflater, container, false)

        food = args.foodObj
        orderQuantity = args.foodOrderQuantity

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFoodStaticInfo()
        setupFavoriteClick()
        setupQuantity()
        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupFoodStaticInfo() {
        binding.apply {
            ivFoodImage.setImageResource(food.imageRes)
            tvRating.text = food.rating.toString()
            tvFoodNameHeader.text = food.name
            tvFoodNameTitle.text = food.name
            tvFoodDescription.text = food.description
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

    private fun setupFavoriteClick() {
        binding.ivHeart.setOnClickListener {
            isFavorite = !isFavorite

            if (isFavorite) {
                binding.ivHeart.setImageResource(R.drawable.ic_heart)
            } else {
                binding.ivHeart.setImageResource(R.drawable.ic_heart_outline)
            }
        }
    }

    private fun setupRecyclerView() {
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

        foodReviewAdapter = FoodReviewAdapter()
        binding.rvReview.apply {
            adapter = foodReviewAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            itemAnimator = null
        }
        foodReviewAdapter.submitList(mockFoodReviews)
    }

    private val mockFoodIngredients = listOf(
        FoodIngredient("1", "Hot sauce", 1.0),
        FoodIngredient("2", "Ketchup", 1.0),
        FoodIngredient("3", "Mustard", 1.0)
    )

    private val mockFoodReviews = listOf(
        FoodReview(
            "1",
            "Example Person",
            4.5,
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore."
        ),
        FoodReview(
            "2",
            "Example Person",
            1.0,
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore."
        ),
        FoodReview(
            "3",
            "Example Person",
            3.0,
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore."
        ),
        FoodReview(
            "4",
            "Example Person",
            5.0,
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore."
        ),
    )

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAddToCart.setOnClickListener {
            //TODO: add food to cart
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}