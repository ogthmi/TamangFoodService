package com.example.tamangfood.presentation.ui.mainapp.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.data.model.Order
import com.example.tamangfood.data.model.Food
import com.example.tamangfood.databinding.FragmentOrderBinding
import com.example.tamangfood.presentation.utils.FoodType
import com.example.tamangfood.presentation.utils.OrderStatus
import com.example.tamangfood.presentation.utils.Utils
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderFragment : Fragment() {
    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var order: List<Order>
    private var selectTab = OrderStatus.ACTIVE
    private val args: OrderFragmentArgs by navArgs()

    companion object {
        private const val STATE_SELECTED_TAB_INDEX = "state_selected_tab_index"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mockData()
        setupUI()
        setupRecyclerView()
        setupTabListeners()
        restoreSelectedTabAndList(savedInstanceState)
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        restoreSelectedTabFromBackStack()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val tabIndex = when (selectTab) {
            OrderStatus.ACTIVE -> 0
            OrderStatus.COMPLETED -> 1
            OrderStatus.CANCELLED -> 2
        }
        outState.putInt(STATE_SELECTED_TAB_INDEX, tabIndex)
        super.onSaveInstanceState(outState)
    }

    private fun setupUI(){
        val isFromDrawer = args.isFromDrawer
        if (isFromDrawer) {
            binding.ivBack.visibility = View.VISIBLE
            Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
        }
        else {
            binding.ivBack.visibility = View.GONE
            Utils.showBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
        }
    }

    private fun setupClickListeners(){
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun persistSelectedTabToBackStack() {
        val tabIndex = when (selectTab) {
            OrderStatus.ACTIVE -> 0
            OrderStatus.COMPLETED -> 1
            OrderStatus.CANCELLED -> 2
        }
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.set(STATE_SELECTED_TAB_INDEX, tabIndex)
    }

    private fun restoreSelectedTabFromBackStack() {
        val tabIndex = findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.get<Int>(STATE_SELECTED_TAB_INDEX)
            ?: return

        val safeIndex = tabIndex.coerceIn(0, binding.tabLayout.tabCount - 1)
        if (binding.tabLayout.selectedTabPosition == safeIndex) return

        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(safeIndex))
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(
            onCancelClick = { order ->
                persistSelectedTabToBackStack()
                findNavController().navigate(
                    OrderFragmentDirections.actionOrderFragmentToCancelOrderFragment(order.id)
                )
            },
            onTrackClick = { order ->
                persistSelectedTabToBackStack()
                findNavController().navigate(
                    OrderFragmentDirections.actionOrderFragmentToDeliveryTrackingFragment(order.id)
                )
            },
            onReviewClick = { order ->
                persistSelectedTabToBackStack()
                findNavController().navigate(
                    OrderFragmentDirections.actionOrderFragmentToLeaveReviewFragment(order.id)
                )
            },
            onOrderAgainClick = { order ->
                persistSelectedTabToBackStack()
                // TODO: Handle order again
            },
            onItemClick = { order ->
                persistSelectedTabToBackStack()
                // TODO: View detail order
            },
            onFoodClick = {food ->
                val action = OrderFragmentDirections.actionOrderFragmentToFoodDetailFragment(
                    food.id,
                    food.quantity
                )
                findNavController().navigate(action)
            }
        )

        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }
    }

    private fun setupTabListeners() {
        // Add tabs
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.active)))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.completed)))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.cancelled)))

        val tabStrip = binding.tabLayout.getChildAt(0) as LinearLayout
        for (i in 0 until tabStrip.childCount) {
            val tab = tabStrip.getChildAt(i)
            val params = tab.layoutParams as LinearLayout.LayoutParams
            params.marginStart = 20
            params.marginEnd = 20
            tab.layoutParams = params
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val status = when (it.position) {
                        0 -> OrderStatus.ACTIVE
                        1 -> OrderStatus.COMPLETED
                        2 -> OrderStatus.CANCELLED
                        else -> OrderStatus.ACTIVE
                    }
                    selectTab = status
                    observeViewModel(status)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // No action needed
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // No action needed
            }
        })
    }

    private fun restoreSelectedTabAndList(savedInstanceState: Bundle?) {
        val restoredIndex =
            findNavController().currentBackStackEntry?.savedStateHandle?.get<Int>(STATE_SELECTED_TAB_INDEX)
                ?: savedInstanceState?.getInt(STATE_SELECTED_TAB_INDEX)
                ?: 0
        val safeIndex = restoredIndex.coerceIn(0, binding.tabLayout.tabCount - 1)

        selectTab = when (safeIndex) {
            0 -> OrderStatus.ACTIVE
            1 -> OrderStatus.COMPLETED
            2 -> OrderStatus.CANCELLED
            else -> OrderStatus.ACTIVE
        }

        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(safeIndex))
        observeViewModel(selectTab)
    }

    private fun observeViewModel(status: OrderStatus) {
        lifecycleScope.launch {
            val filtered = order.filter { status == it.status }
            orderAdapter.submitList(filtered)
            setupEmptyState(filtered.isEmpty())
        }
    }

    private fun setupEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.emptyStateContainer.visibility = View.VISIBLE
            binding.rvOrders.visibility = View.GONE
        } else {
            binding.emptyStateContainer.visibility = View.GONE
            binding.rvOrders.visibility = View.VISIBLE
        }
    }

    private fun mockData(){
        order = listOf(
            // Active orders
            Order(
                id = 1,
                name = "Order #001",
                price = "$20.00",
                dateTime = "29 Nov, 01:20 pm",
                itemCount = 2,
                status = OrderStatus.ACTIVE,
                imageRes = R.drawable.ic_launcher_background,
                items = listOf(
                    Food(
                        1,
                        "Strawberry shake",
                        "$10.00",
                        1,
                        4.5,
                        FoodType.DESSERT,
                        imageRes = R.drawable.ic_launcher_background),
                    Food(2,
                        "Chocolate Cake",
                        "$10.00",
                        10,
                        5.0,
                        FoodType.DRINK,
                        imageRes = R.drawable.ic_launcher_background)
                )
            ),
            // Completed orders
            Order(
                id = 2,
                name = "Order #002",
                price = "$50.00",
                dateTime = "29 Nov, 01:20 pm",
                itemCount = 2,
                status = OrderStatus.COMPLETED,
                imageRes = R.drawable.ic_launcher_background,
                items = listOf(
                    Food(3,
                        "Chicken Curry",
                        "$25.00",
                        1,
                        3.5,
                        FoodType.MEAL,
                        imageRes = R.drawable.ic_launcher_background),
                    Food(4,
                        "Rice Bowl",
                        "$25.00",
                        1,
                        3.0,
                        FoodType.SNACK,
                        imageRes = R.drawable.ic_launcher_background)
                )
            ),
            Order(
                id = 3,
                name = "Order #003",
                price = "$50.00",
                dateTime = "10 Nov, 06:05 pm",
                itemCount = 2,
                status = OrderStatus.COMPLETED,
                imageRes = R.drawable.ic_launcher_background,
                items = listOf(
                    Food(5,
                        "Bean and Vegetable Burger",
                        "$25.00",
                        1,
                        4.0,
                        FoodType.VEGAN,
                        imageRes = R.drawable.ic_launcher_background),
                    Food(
                        6,
                        "French Fries",
                        "$25.00",
                        1,
                        4.2,
                        FoodType.SNACK,
                        imageRes = R.drawable.ic_launcher_background
                    )
                )
            ),
            Order(
                id = 4,
                name = "Order #004",
                price = "$8.00",
                dateTime = "10 Nov, 08:30 am",
                itemCount = 1,
                status = OrderStatus.COMPLETED,
                imageRes = R.drawable.ic_launcher_background,
                items = listOf(
                    Food(
                        7,
                        "Coffee Latte",
                        "$8.00",
                        1,
                        4.6,
                        FoodType.DRINK,
                        imageRes = R.drawable.ic_launcher_background
                    )
                )
            ),
            Order(
                id = 5,
                name = "Order #005",
                price = "$22.00",
                dateTime = "03 Oct, 03:40 pm",
                itemCount = 2,
                status = OrderStatus.COMPLETED,
                imageRes = R.drawable.ic_launcher_background,
                items = listOf(
                    Food(8,
                        "Strawberry Cheesecake",
                        "$12.00",
                        1,
                        5.0,
                        FoodType.DESSERT,
                        imageRes = R.drawable.ic_launcher_background),
                    Food(9,
                        "Ice Cream",
                        "$10.00",
                        1,
                        4.5,
                        FoodType.DESSERT,
                        imageRes = R.drawable.ic_launcher_background)
                )
            ),
            // Cancelled orders
            Order(
                id = 6,
                name = "Order #006",
                price = "$103.00",
                dateTime = "02 Nov, 04:00 pm",
                itemCount = 3,
                status = OrderStatus.CANCELLED,
                imageRes = R.drawable.ic_launcher_background,
                items = listOf(
                    Food(
                        10,
                        "Sushi Roll",
                        "$35.00",
                        2,
                        4.4,
                        FoodType.MEAL,
                        imageRes = R.drawable.ic_launcher_background
                    ),
                    Food(
                        11,
                        "Miso Soup",
                        "$18.00",
                        1,
                        4.1,
                        FoodType.MEAL,
                        imageRes = R.drawable.ic_launcher_background
                    ),
                    Food(
                        12,
                        "Sashimi",
                        "$50.00",
                        1,
                        4.7,
                        FoodType.MEAL,
                        imageRes = R.drawable.ic_launcher_background
                    )
                )
            ),
            Order(
                id = 7,
                name = "Order #007",
                price = "$15.00",
                dateTime = "12 Oct, 03:15 pm",
                itemCount = 2,
                status = OrderStatus.CANCELLED,
                imageRes = R.drawable.ic_launcher_background,
                items = listOf(
                    Food(
                        13,
                        "Fruit and Berry Tea",
                        "$8.00",
                        1,
                        4.3,
                        FoodType.DRINK,
                        imageRes = R.drawable.ic_launcher_background
                    ),
                    Food(
                        14,
                        "Green Tea",
                        "$7.00",
                        1,
                        4.0,
                        FoodType.DRINK,
                        imageRes = R.drawable.ic_launcher_background
                    )
                )
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}