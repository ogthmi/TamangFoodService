package com.example.tamangfood.presentation.ui.mainapp.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.data.model.Food
import com.example.tamangfood.databinding.FragmentMenuBinding
import com.example.tamangfood.domain.model.FoodCategory
import com.example.tamangfood.presentation.ui.mainapp.home.cart.addtocart.AddToCartBottomSheet
import com.example.tamangfood.presentation.utils.FoodType
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MenuViewModel by viewModels()

    private lateinit var menuFoodAdapter: MenuFoodAdapter
    private lateinit var menuFoods: List<Food>

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

        mockMenuFood()
        setupRecyclerView()
        observeCategories()
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
            menuFoodAdapter.submitList(emptyList())
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
                    MenuFragmentDirections.actionMenuFragmentToFoodDetailFragment(selectedFood)
                findNavController().navigate(action)
            },
            onAddToCartClick = { selectedFood ->
                val bottomSheet = AddToCartBottomSheet.newInstance(selectedFood)
                bottomSheet.show(parentFragmentManager, AddToCartBottomSheet.TAG)
            }
        )

        binding.rvMenuFood.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = menuFoodAdapter
        }
    }


    private fun applyCategoryFilter(category: FoodCategory) {
        binding.tvTitle.text = category.name
        val filtered = menuFoods.filter { category.type == it.type }
        menuFoodAdapter.submitList(filtered)
    }

    private fun mockMenuFood() {
        menuFoods = listOf(
            // Snacks
            Food(1, "Mexican Appetizer", "$15.00", 1, 5.0, FoodType.SNACK, "Tortilla Chips with Topping", R.drawable.ic_launcher_background),
            Food(2, "Pork Skewer", "$12.99", 1, 4.5, FoodType.SNACK, "Marinated in a rich blend of herbs and spices, then grilled to perfection.", R.drawable.ic_launcher_background),
            // Meal
            Food(3, "Fresh Prawn Ceviche", "$15.00", 1, 5.0, FoodType.MEAL, "Shrimp marinated in zesty lime juice, mixed with crisp onions, tomatoes, and cilantro.", R.drawable.ic_launcher_background),
            Food(4, "Chicken Burger", "$12.99", 1, 4.5, FoodType.MEAL, "Tender grilled chicken breast, topped with crisp lettuce, tomatoes, creamy mayo.", R.drawable.ic_launcher_background),
            // Vegan
            Food(5, "Mushroom Risotto", "$15.00", 1, 5.0, FoodType.VEGAN, "Creamy mushroom risotto with arborio rice, wild mushrooms, Parmesan, and white wine.", R.drawable.ic_launcher_background),
            Food(6, "Broccoli Lasagna", "$12.99", 1, 4.5, FoodType.VEGAN, "Tender broccoli florets, creamy ricotta, savory marinara, melted mozzarella.", R.drawable.ic_launcher_background),
            // Dessert
            Food(7, "Chocolate Brownie", "$15.00", 1, 5.0, FoodType.DESSERT, "Premium cocoa, melted chocolate, vanilla, moist fudgey center with crisp top.", R.drawable.ic_launcher_background),
            Food(8, "Macarons", "$12.99", 1, 4.5, FoodType.DESSERT, "Delicate vanilla and chocolate macarons, crisp shell and smooth creamy filling.", R.drawable.ic_launcher_background),
            // Drinks
            Food(9, "Iced Coffee", "$15.00", 1, 5.0, FoodType.DRINK, "Cold brew with milk and a hint of vanilla.", R.drawable.ic_launcher_background),
            Food(10, "Fresh Lemonade", "$12.99", 1, 4.5, FoodType.DRINK, "Freshly squeezed lemons with mint and a touch of honey.", R.drawable.ic_launcher_background),
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
