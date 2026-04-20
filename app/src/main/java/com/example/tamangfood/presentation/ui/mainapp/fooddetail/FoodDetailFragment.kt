package com.example.tamangfood.presentation.ui.mainapp.fooddetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentFoodDetailBinding
import com.example.tamangfood.domain.model.FoodDetail
import com.example.tamangfood.presentation.utils.ImageLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils

@AndroidEntryPoint
class FoodDetailFragment : Fragment() {
    private var _binding: FragmentFoodDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FoodDetailViewModel by viewModels()

    private var orderQuantity: Int = 1
    private var maxOrderQuantity: Int = 99
    private var localFavoriteOverride: Boolean? = null

    private lateinit var foodIngredientAdapter: FoodIngredientAdapter
    private lateinit var foodReviewAdapter: FoodReviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodDetailBinding.inflate(inflater, container, false)
        orderQuantity = 1
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupQuantityControls()
        setupFavoriteClick()
        setupClickListeners()
        observeUiState()

        binding.tvFoodDetailError.setOnClickListener { viewModel.reloadDetailAndComments() }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.comments.collect { list ->
                        foodReviewAdapter.submitList(list)
                        binding.rvReview.isVisible = list.isNotEmpty()
                        binding.tvReviewEmpty.isVisible = list.isEmpty()
                    }
                }
                launch {
                    viewModel.addToCartState.collect { state ->
                        when (state) {
                            is NetworkState.Init -> Unit
                            is NetworkState.Loading -> binding.btnAddToCart.isEnabled = false
                            is NetworkState.Success<*> -> {
                                binding.btnAddToCart.isEnabled = true
                                Utils.showToast(requireContext(), "Add to cart successful!")
                                viewModel.clearAddToCartState()
                            }
                            is NetworkState.Error -> {
                                binding.btnAddToCart.isEnabled = true
                                Utils.showToast(requireContext(), state.message)
                                viewModel.clearAddToCartState()
                            }
                        }
                    }
                }
                viewModel.uiState.collect { state ->
                    when (state) {
                        FoodDetailUiState.Loading -> showLoading(true)
                        is FoodDetailUiState.Loaded -> {
                            showLoading(false)
                            bindDetail(state.detail)
                        }
                        is FoodDetailUiState.Failed -> {
                            showLoading(false)
                            binding.scrollContent.isVisible = false
                            binding.tvFoodDetailError.isVisible = true
                            binding.tvFoodDetailError.text =
                                state.message.ifBlank { getString(R.string.food_detail_load_error) }
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.progressFoodDetail.isVisible = loading
        if (loading) {
            binding.scrollContent.isVisible = false
            binding.tvFoodDetailError.isVisible = false
        }
    }

    private fun bindDetail(detail: FoodDetail) {
        localFavoriteOverride = null
        binding.scrollContent.isVisible = true
        binding.tvFoodDetailError.isVisible = false

        maxOrderQuantity = detail.quantity.coerceAtLeast(0)
        orderQuantity = when {
            maxOrderQuantity <= 0 -> 0
            else -> orderQuantity.coerceIn(1, maxOrderQuantity)
        }

        binding.apply {
            tvFoodNameHeader.text = detail.name
            tvFoodNameTitle.text = detail.name
            tvFoodDescription.text = detail.description.orEmpty()
            tvRating.text = detail.avgRating.toString()
            tvFoodPrice.text = "$" + detail.price.toString()
            tvAvailableQuantity.text =
                getString(R.string.available_quantity, maxOrderQuantity)

            if (!detail.urlImage.isNullOrBlank()) {
                ImageLoader.load(requireContext(), ivFoodImage, detail.urlImage)
            } else {
                ivFoodImage.setImageResource(R.drawable.ic_launcher_foreground)
            }

            updateHeartIcon(detail.hasLiked)
        }

        refreshQuantityUi()

        val hasIngredients = detail.ingredients.isNotEmpty()
        binding.tvAddIngredient.isVisible = hasIngredients
        binding.rvIngredients.isVisible = hasIngredients

        val ingredientsUi = if (hasIngredients) {
            detail.ingredients.map {
                FoodIngredient(it.id.toString(), it.name, it.price.toDouble())
            }
        } else {
            emptyList()
        }
        foodIngredientAdapter.submitList(ingredientsUi)
    }

    private fun updateHeartIcon(serverLiked: Boolean) {
        val liked = localFavoriteOverride ?: serverLiked
        binding.ivHeart.setImageResource(
            if (liked) R.drawable.ic_heart else R.drawable.ic_heart_outline
        )
    }

    private fun setupQuantityControls() {
        binding.ivDecrease.setOnClickListener {
            if (maxOrderQuantity > 0 && orderQuantity > 1) {
                orderQuantity--
                refreshQuantityUi()
            }
        }

        binding.ivIncrease.setOnClickListener {
            if (maxOrderQuantity > 0 && orderQuantity < maxOrderQuantity) {
                orderQuantity++
                refreshQuantityUi()
            }
        }

        binding.etQuantity.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val input = binding.etQuantity.text.toString().toIntOrNull() ?: 0
                orderQuantity = when {
                    maxOrderQuantity <= 0 -> 0
                    else -> input.coerceIn(1, maxOrderQuantity)
                }
                refreshQuantityUi()
            }
        }
    }

    private fun refreshQuantityUi() {
        val canDecrease = maxOrderQuantity > 0 && orderQuantity > 1
        val canIncrease = maxOrderQuantity > 0 && orderQuantity < maxOrderQuantity

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

    private fun setupFavoriteClick() {
        binding.ivHeart.setOnClickListener {
            val detail = (viewModel.uiState.value as? FoodDetailUiState.Loaded)?.detail ?: return@setOnClickListener
            val next = !(localFavoriteOverride ?: detail.hasLiked)
            localFavoriteOverride = next
            updateHeartIcon(next)
        }
    }

    private fun setupRecyclerView() {
        foodIngredientAdapter = FoodIngredientAdapter(
            onItemClick = { }
        )
        binding.rvIngredients.apply {
            adapter = foodIngredientAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            itemAnimator = null
        }

        foodReviewAdapter = FoodReviewAdapter()
        binding.rvReview.apply {
            adapter = foodReviewAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            itemAnimator = null
            isNestedScrollingEnabled = false
            setHasFixedSize(false)
        }
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAddToCart.setOnClickListener {
            if (maxOrderQuantity <= 0 || orderQuantity <= 0) {
                Utils.showToast(requireContext(), getString(R.string.food_detail_out_of_stock))
                return@setOnClickListener
            }
            val selectedIngredientIds = foodIngredientAdapter
                .getSelectedIngredients()
                .mapNotNull { it.id.toIntOrNull() }
                .distinct()

            viewModel.addToCart(
                quantity = orderQuantity,
                ingredientIds = selectedIngredientIds
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
