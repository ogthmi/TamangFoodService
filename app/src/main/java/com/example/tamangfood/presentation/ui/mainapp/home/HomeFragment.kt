package com.example.tamangfood.presentation.ui.mainapp.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.data.model.Food
import com.example.tamangfood.databinding.FragmentHomeBinding
import com.example.tamangfood.domain.model.FoodCategory
import com.example.tamangfood.presentation.ui.mainapp.home.cart.CartFragment
import com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.ProfileFragment
import com.example.tamangfood.presentation.utils.FoodType
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.SpacingItem
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : androidx.fragment.app.Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var homeCategoryAdapter: HomeCategoryAdapter
    private lateinit var foodBestSellerAdapter: FoodBestSellerAdapter
    private lateinit var foodRecommendAdapter: FoodRecommendAdapter

    //    private lateinit var drawerCartAdapter: CartAdapter
//    private val cartViewModel: CartViewModel by viewModels()
    var isNavigatingToFragment = false
//    private var isCartDrawerVisible = false
//    private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)

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
        setupDrawer()
        setupClickListeners()
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

    private val bestSellerItems = listOf(
        Food(
            1,
            "Strawberry Cake",
            "$10.3",
            1,
            4.5,
            FoodType.DESSERT,
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore.",
            R.drawable.ic_launcher_background
        ),
        Food(
            2,
            "Cheesy Pizza",
            "$12.9",
            1,
            5.0,
            FoodType.MEAL,
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore.",
            R.drawable.ic_launcher_background
        ),
        Food(
            3,
            "Ice Cream",
            "$8.2",
            1,
            3.0,
            FoodType.DESSERT,
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore.",
            R.drawable.ic_launcher_background
        )
    )
    private val recommendItems = listOf(
        Food(
            4,
            "Big Burger",
            "10.0",
            1,
            3.5,
            FoodType.MEAL,
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore.",
            R.drawable.ic_launcher_background
        ),
        Food(
            5,
            "Spring Rolls",
            "9.5",
            1,
            5.0,
            FoodType.MEAL,
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore.",
            R.drawable.ic_launcher_background
        )
    )

    private fun setupFoodBestSellerRecyclerViews() {
        val context = requireContext()

        foodBestSellerAdapter = FoodBestSellerAdapter(
            onItemClick = { selectedFood ->
                Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
                val action = HomeFragmentDirections
                    .actionHomeFragmentToFoodDetailFragment(
                        selectedFood.id,
                        selectedFood.quantity
                    )
                findNavController().navigate(action)
            },
        )
        binding.rvBestSeller.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = foodBestSellerAdapter
            val space = resources.getDimensionPixelSize(R.dimen.space)
            addItemDecoration(SpacingItem(space))
        }
        foodBestSellerAdapter.submitList(bestSellerItems)


    }

    private fun setupFoodRecommendRecyclerViews() {

        foodRecommendAdapter = FoodRecommendAdapter(
            onItemClick = { selectedFood ->
                Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
                val action = HomeFragmentDirections
                    .actionHomeFragmentToFoodDetailFragment(
                        selectedFood.id,
                        selectedFood.quantity
                    )
                findNavController().navigate(action)
            }
        )
        binding.rvRecommend.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = foodRecommendAdapter
            val space = resources.getDimensionPixelSize(R.dimen.space)
            addItemDecoration(SpacingItem(space))
        }
        foodRecommendAdapter.submitList(recommendItems)
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
}
