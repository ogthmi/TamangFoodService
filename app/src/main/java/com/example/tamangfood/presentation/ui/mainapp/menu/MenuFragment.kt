package com.example.tamangfood.presentation.ui.mainapp.menu

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.data.model.Food
import com.example.tamangfood.databinding.FragmentMenuBinding
import com.example.tamangfood.presentation.utils.FoodType
import com.example.tamangfood.presentation.utils.OrderStatus
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
        setupTabLayout()
    }

    private fun setupTabLayout() {

        FoodType.values().forEach { type ->
            val tab = binding.tabLayout.newTab()
            tab.customView = createTabView(type.tabSelector)
            binding.tabLayout.addTab(tab)
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab ?: return
                observeViewModel(FoodType.values()[tab.position])
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.tabLayout.getTabAt(0)?.let {
            binding.tabLayout.selectTab(it)
            observeViewModel(FoodType.values()[it.position])
        }
    }

    private fun createTabView(icon: Int): View {
        val view = layoutInflater.inflate(
            R.layout.item_menu_category_tab,
            binding.tabLayout,
            false
        )
        val iconView = view.findViewById<ImageView>(R.id.iv_icon)
        iconView.setImageResource(icon)
        return view
    }

    private fun setupRecyclerView() {
        menuFoodAdapter = MenuFoodAdapter(onItemClick = { })
        binding.rvMenuFood.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = menuFoodAdapter
        }
    }


    private fun observeViewModel(type: FoodType) {
        binding.tvTitle.text = type.title.let { getString(it) }
        lifecycleScope.launch {
            val filtered = menuFoods.filter { type == it.type }
            menuFoodAdapter.submitList(filtered)
        }
    }

    private fun mockMenuFood(){
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
