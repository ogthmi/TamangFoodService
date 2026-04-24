package com.example.tamangfood.presentation.ui.mainapp.home.filter

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
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentFilterBinding
import com.example.tamangfood.domain.model.FoodCategory
import com.example.tamangfood.domain.model.FoodCategoryDetail
import com.example.tamangfood.presentation.ui.mainapp.favorite.FoodPagingAdapter
import com.example.tamangfood.presentation.ui.mainapp.home.cart.addtocart.AddToCartBottomSheet
import com.example.tamangfood.presentation.utils.GridSpacingItem
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilterFragment : Fragment(R.layout.fragment_filter) {
    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FilterViewModel by viewModels()

    private lateinit var foodTypeAdapter: FoodTypeAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var filterResultAdapter: FoodPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        setupRecyclerView()
        setupFilterResultRecyclerView()
        observeCategoriesState()
        observeDetailsState()
        observeFilterResults()
        observeFavorite()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnApply.setOnClickListener {
            val selectedIds = categoryAdapter.getSelectedItems().map { it.id.toLong() }
            val rating = binding.rtTopRated.rating.toInt()
            viewModel.applyFilter(selectedIds, rating)
        }
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter { }

        binding.rvCategories.apply {
            adapter = categoryAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            itemAnimator = null
        }

        foodTypeAdapter = FoodTypeAdapter { category ->
            viewModel.loadCategoryDetails(category.id)
        }

        binding.rvFoodTypes.apply {
            adapter = foodTypeAdapter
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )
        }
    }

    private fun setupFilterResultRecyclerView() {
        filterResultAdapter = FoodPagingAdapter(
            onItemClick = { food ->
                Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
                val action = FilterFragmentDirections.actionFilterFragmentToFoodDeailFragment(food.id)
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

        binding.rvFilterResults.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = filterResultAdapter
            val spacing = resources.getDimensionPixelSize(R.dimen.space)
            addItemDecoration(GridSpacingItem(2, spacing))
        }
    }

    private fun observeFilterResults() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filterFoods.collectLatest { pagingData ->
                filterResultAdapter.submitData(pagingData)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            filterResultAdapter.loadStateFlow.collectLatest { loadState ->
                val refresh = loadState.refresh
                val isEmpty = refresh is LoadState.NotLoading && filterResultAdapter.itemCount == 0

                binding.progressFilterResults.isVisible = refresh is LoadState.Loading
                binding.rvFilterResults.isVisible = !isEmpty && refresh !is LoadState.Loading
                binding.tvFilterEmpty.isVisible = isEmpty
            }
        }
    }

    private fun observeFavorite() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteState.collectLatest { message ->
                Utils.showToast(requireContext(), message)
            }
        }
    }

    private fun observeCategoriesState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoriesState.collect { state ->
                    when (state) {
                        is NetworkState.Loading, NetworkState.Init -> {
                            binding.progressBarType.visibility = View.VISIBLE
                            viewModel.resetDetailsState()
                        }
                        is NetworkState.Success<*> -> {
                            binding.progressBarType.visibility = View.GONE
                            @Suppress("UNCHECKED_CAST")
                            val raw = state.data as? List<FoodCategory> ?: emptyList()
                            val ordered = raw.sortedWith(
                                compareBy<FoodCategory> { it.type.ordinal }.thenBy { it.name }
                            )
                            foodTypeAdapter.submitList(ordered)
                            binding.rvFoodTypes.post {
                                foodTypeAdapter.resetSelectionToFirst()
                            }
                            if (ordered.isEmpty()) {
                                viewModel.resetDetailsState()
                                applyDetailsUi(NetworkState.Init)
                            } else {
                                viewModel.loadCategoryDetails(ordered.first().id)
                            }
                        }
                        is NetworkState.Error -> {
                            binding.progressBarType.visibility = View.GONE
                            foodTypeAdapter.submitList(emptyList())
                            viewModel.resetDetailsState()
                            Utils.showToast(requireContext(), state.message)
                        }
                    }
                }
            }
        }
    }

    private fun observeDetailsState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.detailsState.collect { state ->
                    applyDetailsUi(state)
                }
            }
        }
    }

    private fun applyDetailsUi(state: NetworkState) {
        when (state) {
            is NetworkState.Init -> {
                binding.progressBarDetails.visibility = View.GONE
                categoryAdapter.clearSelection()
                categoryAdapter.submitList(emptyList())
            }
            is NetworkState.Loading -> {
                binding.progressBarDetails.visibility = View.VISIBLE
            }
            is NetworkState.Success<*> -> {
                binding.progressBarDetails.visibility = View.GONE
                categoryAdapter.clearSelection()
                @Suppress("UNCHECKED_CAST")
                val list = state.data as? List<FoodCategoryDetail> ?: emptyList()
                categoryAdapter.submitList(list)
            }
            is NetworkState.Error -> {
                binding.progressBarDetails.visibility = View.GONE
                categoryAdapter.clearSelection()
                categoryAdapter.submitList(emptyList())
                Utils.showToast(requireContext(), state.message)
            }
        }
    }
}
