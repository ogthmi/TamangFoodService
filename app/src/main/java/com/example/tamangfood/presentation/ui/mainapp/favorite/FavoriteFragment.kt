package com.example.tamangfood.presentation.ui.mainapp.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.filter
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.data.model.Food
import com.example.tamangfood.databinding.FragmentFavoriteBinding
import com.example.tamangfood.presentation.ui.mainapp.home.cart.addtocart.AddToCartBottomSheet
import com.example.tamangfood.presentation.utils.GridSpacingItem
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteViewModel by viewModels()
    private lateinit var adapter: FoodPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Utils.showBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))

        setupRecyclerView()
        observePaging()
        observeFavorite()
    }

    private fun setupRecyclerView() {
        adapter = FoodPagingAdapter(
            onItemClick = { food ->
                Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
                val action =
                    FavoriteFragmentDirections.actionFavoriteFragmentToFoodDeailFragment(food.id)
                findNavController().navigate(action)
            },
            onFavoriteClick = { food ->
                viewModel.toggleFavorite(food)
            },
            onAddToCartClick = { food ->
                val bottomSheet = AddToCartBottomSheet.newInstance(food)
                bottomSheet.show(parentFragmentManager, AddToCartBottomSheet.TAG)
            }
        )

        binding.rvRecommendGrid.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@FavoriteFragment.adapter

            val spacing = resources.getDimensionPixelSize(R.dimen.space)
            addItemDecoration(GridSpacingItem(2, spacing))
        }
    }

    private fun observePaging() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteFoods.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadState ->
                val isEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
                binding.tvSubtitle.isVisible = isEmpty
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.needsRefresh.collect { needs ->
                if (needs) {
                    adapter.refresh()
                    viewModel.consumeRefresh()
                }
            }
        }
    }

    private fun observeFavorite() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteState.collect { message ->
                Utils.showToast(requireContext(), message)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}