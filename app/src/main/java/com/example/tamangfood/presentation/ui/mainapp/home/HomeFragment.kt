package com.example.tamangfood.presentation.ui.mainapp.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentHomeBinding
import com.example.tamangfood.domain.model.Food
import com.example.tamangfood.domain.model.FoodCategory
import com.example.tamangfood.presentation.ui.mainapp.home.bestseller.FoodBestSellerAdapter
import com.example.tamangfood.presentation.ui.mainapp.home.cart.CartFragment
import com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.ProfileFragment
import com.example.tamangfood.presentation.ui.mainapp.home.recommend.FoodRecommendAdapter
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.SpacingItem
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : androidx.fragment.app.Fragment() {

    private companion object {
        private const val MAX_HOME_BEST_SELLER_DISPLAY = 5
        private const val MAX_HOME_RECOMMEND_DISPLAY = 4
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var homeCategoryAdapter: HomeCategoryAdapter
    private lateinit var foodBestSellerAdapter: FoodBestSellerAdapter
    private lateinit var foodRecommendAdapter: FoodRecommendAdapter

    private var bestSellerFoodsFull: List<Food> = emptyList()
    private var recommendFoodsFull: List<Food> = emptyList()

    var isNavigatingToFragment = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.showBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))

        setupDrawerWidth()
        setupCategoryRecyclerView()
        observeCategories()
        setupFoodBestSellerRecyclerViews()
        setupFoodRecommendRecyclerViews()
        observeBestSellerFoods()
        observeRecommendFoods()
        setupDrawer()
        setupClickListeners()
        setupFoodSearchFilter()
    }

    private fun setupCategoryRecyclerView() {
        homeCategoryAdapter = HomeCategoryAdapter()
        binding.rvCategories.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = homeCategoryAdapter
        }
    }

    private fun observeCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.categoriesState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> Unit
                        is NetworkState.Success<*> -> {
                            @Suppress("UNCHECKED_CAST")
                            val list = state.data as? List<FoodCategory> ?: emptyList()
                            homeCategoryAdapter.submitList(list)
                        }
                        is NetworkState.Error ->
                            Utils.showToast(requireContext(), state.message)
                    }
                }
            }
        }
    }

    private fun observeBestSellerFoods() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.bestSellerState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> {
                            binding.progressBestSellerHome.isVisible = true
                            binding.rvBestSeller.isVisible = false
                            binding.tvEmptyBestSeller.isVisible = false
                        }
                        is NetworkState.Success<*> -> {
                            binding.progressBestSellerHome.isVisible = false
                            binding.rvBestSeller.isVisible = true
                            @Suppress("UNCHECKED_CAST")
                            val fullList = state.data as? List<Food> ?: emptyList()
                            bestSellerFoodsFull = fullList
                            applyFoodSearchFilterToLists()
                        }
                        is NetworkState.Error -> {
                            binding.progressBestSellerHome.isVisible = false
                            binding.rvBestSeller.isVisible = true
                            Utils.showToast(requireContext(), state.message)
                            bestSellerFoodsFull = emptyList()
                            applyFoodSearchFilterToLists()
                        }
                    }
                }
            }
        }
    }

    private fun observeRecommendFoods() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.recommendState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> {
                            binding.progressRecommendHome.isVisible = true
                            binding.rvRecommend.isVisible = false
                            binding.tvEmptyRecommend.isVisible = false
                        }
                        is NetworkState.Success<*> -> {
                            binding.progressRecommendHome.isVisible = false
                            binding.rvRecommend.isVisible = true
                            @Suppress("UNCHECKED_CAST")
                            val fullList = state.data as? List<Food> ?: emptyList()
                            recommendFoodsFull = fullList
                            applyFoodSearchFilterToLists()
                        }
                        is NetworkState.Error -> {
                            binding.progressRecommendHome.isVisible = false
                            binding.rvRecommend.isVisible = true
                            Utils.showToast(requireContext(), state.message)
                            recommendFoodsFull = emptyList()
                            applyFoodSearchFilterToLists()
                        }
                    }
                }
            }
        }
    }

    private fun setupFoodBestSellerRecyclerViews() {
        val context = requireContext()

        foodBestSellerAdapter = FoodBestSellerAdapter(
            onItemClick = { selectedFood ->
                Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
                val action = HomeFragmentDirections
                    .actionHomeFragmentToFoodDetailFragment(
                        selectedFood.id
                    )
                findNavController().navigate(action)
            },
        )
        binding.rvBestSeller.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = foodBestSellerAdapter
            overScrollMode = View.OVER_SCROLL_NEVER
            val space = resources.getDimensionPixelSize(R.dimen.space)
            addItemDecoration(SpacingItem(space))
        }
    }

    private fun setupFoodRecommendRecyclerViews() {
        foodRecommendAdapter = FoodRecommendAdapter(
            onItemClick = { selectedFood ->
                Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
                val action = HomeFragmentDirections
                    .actionHomeFragmentToFoodDetailFragment(
                        selectedFood.id
                    )
                findNavController().navigate(action)
            }
        )
        binding.rvRecommend.apply {
            layoutManager = GridLayoutManager(context, 2).apply {
                isAutoMeasureEnabled = true
            }
            adapter = foodRecommendAdapter
            isNestedScrollingEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
            setHasFixedSize(false)
            itemAnimator = null
            val space = resources.getDimensionPixelSize(R.dimen.space)
            addItemDecoration(SpacingItem(space))
        }
    }

    private fun setupDrawer() {
        binding.fragmentHome.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
                Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
            }

            override fun onDrawerClosed(drawerView: View) {
                if (!isNavigatingToFragment) {
                    Utils.showBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
                }
                isNavigatingToFragment = false
            }

            override fun onDrawerStateChanged(newState: Int) {
            }
        })
    }

    private fun openDrawerFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.drawer_container, fragment)
            .commit()

        binding.fragmentHome.openDrawer(GravityCompat.END)
    }

    private fun setupFoodSearchFilter() {
        binding.etSearch.doAfterTextChanged {
            applyFoodSearchFilterToLists()
        }
    }

    private fun applyFoodSearchFilterToLists() {
        val q = binding.etSearch.text?.toString().orEmpty().trim()
        val hasQuery = q.isNotEmpty()
        val bestFiltered = bestSellerFoodsFull.filterByFoodName(q).take(MAX_HOME_BEST_SELLER_DISPLAY)
        val recommendFiltered = recommendFoodsFull.filterByFoodName(q).take(MAX_HOME_RECOMMEND_DISPLAY)
        foodBestSellerAdapter.submitList(bestFiltered)
        foodRecommendAdapter.submitList(recommendFiltered)
        updateHomeFoodEmptyUi(
            bestFiltered.isEmpty(),
            hasQuery,
            binding.tvEmptyBestSeller,
            binding.rvBestSeller
        )
        updateHomeFoodEmptyUi(
            recommendFiltered.isEmpty(),
            hasQuery,
            binding.tvEmptyRecommend,
            binding.rvRecommend
        )
    }

    private fun updateHomeFoodEmptyUi(
        isEmpty: Boolean,
        hasSearchQuery: Boolean,
        emptyView: TextView,
        listView: RecyclerView
    ) {
        if (isEmpty) {
            emptyView.text = if (hasSearchQuery) {
                getString(R.string.menu_food_search_empty)
            } else {
                getString(R.string.home_no_items_hint)
            }
            emptyView.isVisible = true
            listView.isVisible = false
        } else {
            emptyView.isVisible = false
            listView.isVisible = true
        }
    }

    private fun List<Food>.filterByFoodName(query: String): List<Food> {
        if (query.isEmpty()) return this
        return filter { it.name.contains(query, ignoreCase = true) }
    }

    private fun setupClickListeners() {
        binding.btnViewAllBestSeller.setOnClickListener {
            isNavigatingToFragment = true
            Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToBestSellerFragment())
        }

        binding.btnViewAllRecommend.setOnClickListener {
            isNavigatingToFragment = true
            Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRecommendFragment())
        }

        binding.ivProfileMenu.setOnClickListener {
            openDrawerFragment(ProfileFragment())
        }

        binding.ivCart.setOnClickListener {
            openDrawerFragment(CartFragment())
            binding.fragmentHome.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN)
        }

        binding.ivFilter.setOnClickListener {
            Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
            findNavController().navigate(R.id.action_homeFragment_to_filterFragment)
        }
    }

    private fun setupDrawerWidth() {
        val drawer = binding.drawerContainer

        val screenWidth = resources.displayMetrics.widthPixels
        val drawerWidth = (screenWidth * 4) / 5

        drawer.layoutParams.width = drawerWidth
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        Utils.showBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
    }
}
