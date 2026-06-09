package com.example.tamangfood.presentation.ui.mainapp.home.recommend

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
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentRecommendBinding
import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.presentation.ui.mainapp.FoodAdapter
import com.example.tamangfood.presentation.ui.mainapp.home.cart.addtocart.AddToCartBottomSheet
import com.example.tamangfood.presentation.utils.GridSpacingItem
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecommendFragment : Fragment() {
    private var _binding: FragmentRecommendBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecommendViewModel by viewModels()
    private lateinit var adapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        observeRecommendedFoods()
        observeFavorite()
        setUpClickListeners()
    }

    private fun setUpClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setUpRecyclerView() {
        adapter = FoodAdapter(
            onItemClick = { selectedFood ->
                Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
                val action =
                    RecommendFragmentDirections.actionRecommendFragmentToFoodDetailFragment(
                        selectedFood.id
                    )
                findNavController().navigate(action)
            },
            onFavoriteClick = { selectedFood -> viewModel.toggleFavorite(selectedFood) },
            onAddToCartClick = { selectedFood ->
                val bottomSheet = AddToCartBottomSheet.newInstance(selectedFood)
                bottomSheet.show(parentFragmentManager, AddToCartBottomSheet.TAG)
            }
        )

        binding.rvRecommendGrid.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@RecommendFragment.adapter
            val space = resources.getDimensionPixelSize(R.dimen.space)
            addItemDecoration(GridSpacingItem(2, space))
        }
    }

    private fun observeFavorite() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteState.collect { message ->
                Utils.showToast(requireContext(), message)
            }
        }
    }

    private fun observeRecommendedFoods() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> {
                            binding.progressRecommend.isVisible = true
                            binding.content.isVisible = false
                        }
                        is NetworkState.Success<*> -> {
                            binding.progressRecommend.isVisible = false
                            binding.content.isVisible = true
                            @Suppress("UNCHECKED_CAST")
                            val list = state.data as? List<Food> ?: emptyList()
                            adapter.submitList(list)
                        }
                        is NetworkState.Error -> {
                            binding.progressRecommend.isVisible = false
                            binding.content.isVisible = true
                            Utils.showToast(requireContext(), state.message)
                            adapter.submitList(emptyList())
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
