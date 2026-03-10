package com.example.tamangfood.presentation.ui.mainapp.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentHomeBinding
import com.example.tamangfood.presentation.utils.SpacingItem
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : androidx.fragment.app.Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var foodBestSellerAdapter: FoodBestSellerAdapter
    private lateinit var foodRecommendAdapter: FoodRecommendAdapter
    private var isNavigatingToFragment = false

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
        setupFoodBestSellerRecyclerViews()
        setupFoodRecommendRecyclerViews()
        setupDrawer()
        setupDrawerListener()
        setupClickListeners()
    }

    private fun setupFoodBestSellerRecyclerViews() {
        val context = requireContext()

        // Dummy data; replace with real data later
        val bestSellerItems = listOf(
            FoodItem(1, "Strawberry Cake", "$10.3", R.drawable.ic_launcher_background),
            FoodItem(2, "Cheesy Pizza", "$12.9", R.drawable.ic_launcher_background),
            FoodItem(3, "Ice Cream", "$8.2", R.drawable.ic_launcher_background)
        )

        foodBestSellerAdapter = FoodBestSellerAdapter(
            onItemClick = {

            }
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

    private fun setupFoodRecommendRecyclerViews(){
        val recommendItems = listOf(
            FoodItem(4, "Big Burger", "$10.0", R.drawable.ic_launcher_background),
            FoodItem(5, "Spring Rolls", "$9.5", R.drawable.ic_launcher_background)
        )

        foodRecommendAdapter = FoodRecommendAdapter(
            onItemClick = {}
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
        binding.ivProfileMenu.setOnClickListener {
            Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
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

    private fun setupDrawerListener(){
        binding.menuMyProfile.setOnClickListener {
            isNavigatingToFragment = true
            binding.drawerLayout.closeDrawer(GravityCompat.END)
            Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToUpdateMyProfileFragment())
        }

        binding.menuMyOrders.setOnClickListener {  }

        binding.menuDeliveryAddress.setOnClickListener {
            isNavigatingToFragment = true
            binding.drawerLayout.closeDrawer(GravityCompat.END)
            Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToDeliveryAddressFragment())
        }

        binding.menuPaymentMethods.setOnClickListener {  }

        binding.menuSetting.setOnClickListener {
            isNavigatingToFragment = true
            binding.drawerLayout.closeDrawer(GravityCompat.END)
            Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSettingsFragment())
        }

        binding.menuLogout.setOnClickListener {  }
    }

    private fun setupClickListeners() {
        binding.btnViewAllBestSeller.setOnClickListener {
            isNavigatingToFragment = true
            Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToBestSellerFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
