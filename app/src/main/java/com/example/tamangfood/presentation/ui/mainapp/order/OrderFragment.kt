package com.example.tamangfood.presentation.ui.mainapp.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentOrderBinding
import com.example.tamangfood.domain.model.Order
import com.example.tamangfood.presentation.utils.AppPreferences
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.OrderStatus
import com.example.tamangfood.presentation.utils.Utils
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderFragment : Fragment() {
    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!
    private val viewModel: OrderViewModel by viewModels()
    private lateinit var orderAdapter: OrderAdapter
    private var selectTab = OrderStatus.ACTIVE
    private val args: OrderFragmentArgs by navArgs()
    private val cachedOrders = mutableMapOf<OrderStatus, List<Order>>()
    private val requestedStatuses = mutableSetOf<OrderStatus>()

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
        setupUI()
        setupRecyclerView()
        setupTabListeners()
        observeOrders()
        restoreSelectedTabAndList(savedInstanceState)
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        val cancelled = findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.remove<Boolean>("order_cancelled") ?: false
        if (cancelled) {
            cachedOrders.remove(OrderStatus.ACTIVE)
            cachedOrders.remove(OrderStatus.CANCELLED)
            requestedStatuses.remove(OrderStatus.ACTIVE)
            requestedStatuses.remove(OrderStatus.CANCELLED)
            if (selectTab == OrderStatus.ACTIVE) {
                val userId = AppPreferences.getUserId() ?: -1
                requestedStatuses.add(OrderStatus.ACTIVE)
                viewModel.loadOrders(OrderStatus.ACTIVE, userId)
            }
        }
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
            onReviewClick = { order, food ->
                persistSelectedTabToBackStack()
                findNavController().navigate(
                    OrderFragmentDirections.actionOrderFragmentToLeaveReviewFragment(
                        order.id,
                        food.id,
                        food.name,
                        food.urlImage ?: ""
                    )
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
                    food.id
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
                    requestOrders(status)
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
        requestOrders(selectTab)
    }

    private fun observeOrders() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ordersState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> {
                            renderLoading(true)
                        }
                        is NetworkState.Error -> {
                            renderLoading(false)
                            requestedStatuses.remove(selectTab)
                            Utils.showToast(requireContext(), state.message)
                            val cached = cachedOrders[selectTab].orEmpty()
                            orderAdapter.submitList(cached)
                            setupEmptyState(cached.isEmpty())
                        }
                        is NetworkState.Success<*> -> {
                            renderLoading(false)
                            val data = state.data as? OrdersByStatusData ?: return@collect
                            cachedOrders[data.status] = data.orders
                            requestedStatuses.remove(data.status)
                            if (data.status == selectTab) {
                                orderAdapter.submitList(data.orders)
                                setupEmptyState(data.orders.isEmpty())
                            }
                        }
                    }
                }
            }
        }
    }

    private fun requestOrders(status: OrderStatus) {
        val cached = cachedOrders[status]
        if (cached != null) {
            renderLoading(false)
            orderAdapter.submitList(cached)
            setupEmptyState(cached.isEmpty())
            return
        }
        if (requestedStatuses.contains(status)) return
        val userId = AppPreferences.getUserId() ?: -1
        requestedStatuses.add(status)
        viewModel.loadOrders(status, userId)
    }

    private fun renderLoading(show: Boolean) {
        binding.progressOrders.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvOrders.visibility = if (show) View.GONE else View.VISIBLE
        if (show) {
            binding.emptyStateContainer.visibility = View.GONE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}