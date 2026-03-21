package com.example.tamangfood.presentation.ui.mainapp.home.bestseller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.data.model.Food
import com.example.tamangfood.databinding.FragmentBestSellerBinding
import com.example.tamangfood.presentation.ui.mainapp.FoodAdapter
import com.example.tamangfood.presentation.ui.mainapp.home.cart.addtocart.AddToCartBottomSheet
import com.example.tamangfood.presentation.ui.mainapp.home.recommend.RecommendFragmentDirections
import com.example.tamangfood.presentation.utils.FoodType
import com.example.tamangfood.presentation.utils.GridSpacingItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BestSellerFragment : Fragment() {
    private var _binding: FragmentBestSellerBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FoodAdapter
    private lateinit var bestSellerList: List<Food>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBestSellerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mockData()
        setUpClickListeners()
        setUpRecyclerView()
    }

    private fun setUpClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setUpRecyclerView(){
        adapter = FoodAdapter(
            onItemClick = { selectedFood ->
                val action =
                    BestSellerFragmentDirections.actionBestSellerFragmentToFoodDetailFragment(
                        selectedFood
                    )
                findNavController().navigate(action)
            },
            onFavoriteClick = { },
            onAddToCartClick = { selectedFood ->
                val bottomSheet = AddToCartBottomSheet.newInstance(selectedFood)
                bottomSheet.show(parentFragmentManager, AddToCartBottomSheet.TAG)
            }
        )

        binding.rvBestSellerGrid.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@BestSellerFragment.adapter
            val space = resources.getDimensionPixelSize(R.dimen.space)
            addItemDecoration(GridSpacingItem(2, space))
        }

        adapter.submitList(bestSellerList)
    }

    private fun mockData(){
        bestSellerList = listOf(
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

