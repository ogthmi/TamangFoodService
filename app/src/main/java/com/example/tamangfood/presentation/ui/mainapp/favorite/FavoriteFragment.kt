package com.example.tamangfood.presentation.ui.mainapp.favorite

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.data.model.Food
import com.example.tamangfood.databinding.FragmentFavoriteBinding
import com.example.tamangfood.presentation.ui.mainapp.FoodAdapter
import com.example.tamangfood.presentation.ui.mainapp.home.cart.addtocart.AddToCartBottomSheet
import com.example.tamangfood.presentation.utils.FoodType
import com.example.tamangfood.presentation.utils.GridSpacingItem
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var foodAdapter: FoodAdapter
    private lateinit var favoriteFoodList: List<Food>

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.showBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))

        mockData()
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView(){
        foodAdapter = FoodAdapter(
            onItemClick = {food ->
                Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
                val action = FavoriteFragmentDirections.actionFavoriteFragmentToFoodDeailFragment(food)
                findNavController().navigate(action)
            },
            onFavoriteClick = {},
            onAddToCartClick = { selectedFood ->
                val bottomSheet = AddToCartBottomSheet.newInstance(selectedFood)
                bottomSheet.show(parentFragmentManager, AddToCartBottomSheet.TAG)
            },
        )

        binding.rvRecommendGrid.apply {
            adapter = foodAdapter

            val spacing = resources.getDimensionPixelSize(R.dimen.space)
            layoutManager = GridLayoutManager(requireContext(), 2 )
            addItemDecoration(GridSpacingItem(2, spacing))
        }

        foodAdapter.submitList(favoriteFoodList)
    }

    private fun mockData(){
        favoriteFoodList = listOf(
            Food(
                id = 1,
                name = "Sunny Bruschetta",
                price = "$15.00",
                quantity = 5,
                rating = 4.0,
                type = FoodType.SNACK,
                description = "Lorem ipsum dolor sit amet, consectetur…",
                imageRes = R.drawable.ic_launcher_background
            ),
            Food(
                id = 2,
                name = "Gourmet Grilled Skewers",
                price = "$12.00",
                quantity = 8,
                rating = 4.5,
                type = FoodType.MEAL,
                description = "Lorem ipsum dolor sit amet, consectetur…",
                imageRes = R.drawable.ic_launcher_background
            ),
            Food(
                id = 3,
                name = "Barbecue tacos",
                price = "$15.00",
                quantity = 10,
                rating = 4.0,
                type = FoodType.SNACK,
                description = "Lorem ipsum dolor sit amet, consectetur…",
                imageRes = R.drawable.ic_launcher_background
            ),
            Food(
                id = 4,
                name = "Broccoli lasagna",
                price = "$12.00",
                quantity = 6,
                rating = 3.5,
                type = FoodType.VEGAN,
                description = "Lorem ipsum dolor sit amet, consectetur…",
                imageRes = R.drawable.ic_launcher_background
            ),
            Food(
                id = 5,
                name = "Iced coffee",
                price = "$15.00",
                quantity = 12,
                rating = 5.0,
                type = FoodType.DRINK,
                description = "Lorem ipsum dolor sit amet, consectetur…",
                imageRes = R.drawable.ic_launcher_background
            ),
            Food(
                id = 6,
                name = "Strawberry cake",
                price = "$12.00",
                quantity = 7,
                rating = 4.8,
                type = FoodType.DESSERT,
                description = "Lorem ipsum dolor sit amet, consectetur…",
                imageRes = R.drawable.ic_launcher_background
            ),
        )
    }
}