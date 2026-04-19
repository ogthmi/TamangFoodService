package com.example.tamangfood.presentation.ui.mainapp.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentMenuBinding
import com.example.tamangfood.domain.model.FoodCategory
import com.example.tamangfood.presentation.ui.mainapp.home.cart.addtocart.AddToCartBottomSheet
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MenuViewModel by viewModels()

    private lateinit var menuFoodAdapter: MenuFoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.showBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))

        setupMenuSearch()
        setupRecyclerView()
        observeMenuFoodLoadAndEmpty()
        observeCategories()
        observePagingFoods()
    }

    private fun observeCategories() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoriesState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> Unit
                        is NetworkState.Success<*> -> {
                            @Suppress("UNCHECKED_CAST")
                            val list = state.data as? List<FoodCategory> ?: emptyList()
                            rebuildTabLayout(list)
                        }
                        is NetworkState.Error ->
                            Utils.showToast(requireContext(), state.message)
                    }
                }
            }
        }
    }

    private fun observePagingFoods() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.menuFoodsPaging.collectLatest { pagingData ->
                    menuFoodAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
                }
            }
        }
    }

    private fun setupMenuSearch() {
        binding.etSearch.doAfterTextChanged { editable ->
            viewModel.setMenuSearchQuery(editable?.toString().orEmpty())
        }
    }

    private fun rebuildTabLayout(categories: List<FoodCategory>) {
        binding.tabLayout.clearOnTabSelectedListeners()
        binding.tabLayout.removeAllTabs()
        categories.forEach { category ->
            val tab = binding.tabLayout.newTab()
            tab.customView = createTabView(category.type.tabSelector)
            binding.tabLayout.addTab(tab)
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab ?: return
                categories.getOrNull(tab.position)?.let { applyCategoryFilter(it) }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        if (categories.isNotEmpty()) {
            applyCategoryFilter(categories.first())
            binding.tabLayout.getTabAt(0)?.let { binding.tabLayout.selectTab(it) }
        } else {
            binding.tvTitle.text = ""
            viewModel.clearMenuFoods()
        }
    }

    private fun createTabView(iconRes: Int): View {
        val view = layoutInflater.inflate(
            R.layout.item_menu_category_tab,
            binding.tabLayout,
            false
        )
        view.findViewById<ImageView>(R.id.iv_icon).setImageResource(iconRes)
        return view
    }

    private fun setupRecyclerView() {
        menuFoodAdapter = MenuFoodAdapter(
            onItemClick = { selectedFood ->
                Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
                val action =
                    MenuFragmentDirections.actionMenuFragmentToFoodDetailFragment(
                        selectedFood.id,
                        selectedFood.quantity
                    )
                findNavController().navigate(action)
            },
            onAddToCartClick = { selectedFood ->
//                val bottomSheet = AddToCartBottomSheet.newInstance(selectedFood)
//                bottomSheet.show(parentFragmentManager, AddToCartBottomSheet.TAG)
            }
        )

        binding.rvMenuFood.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = menuFoodAdapter
        }
    }

    private fun observeMenuFoodLoadAndEmpty() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    menuFoodAdapter.loadStateFlow,
                    viewModel.menuSearchQuery
                ) { loadState, rawQuery -> loadState to rawQuery }
                    .collect { (loadState, rawQuery) ->
                        val refresh = loadState.refresh
                        val append = loadState.append
                        val empty = menuFoodAdapter.itemCount == 0
                        val searching = rawQuery.trim().isNotEmpty()

                        binding.progressMenuFoods.isVisible =
                            refresh is LoadState.Loading && empty
                        binding.progressMenuLoadMore.isVisible = append is LoadState.Loading

                        val showEmpty =
                            empty &&
                                refresh is LoadState.NotLoading &&
                                (searching || append.endOfPaginationReached)

                        if (refresh is LoadState.Error && empty) {
                            binding.tvMenuFoodEmpty.isVisible = false
                            Utils.showToast(requireContext(), refresh.error.message ?: "Error")
                        } else {
                            binding.tvMenuFoodEmpty.isVisible = showEmpty
                            if (showEmpty) {
                                binding.tvMenuFoodEmpty.setText(
                                    if (searching) R.string.menu_food_search_empty
                                    else R.string.menu_food_list_empty
                                )
                            }
                        }
                    }
            }
        }
    }

    private fun applyCategoryFilter(category: FoodCategory) {
        binding.etSearch.setText("")
        binding.tvTitle.text = category.name
        viewModel.selectCategory(category)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
