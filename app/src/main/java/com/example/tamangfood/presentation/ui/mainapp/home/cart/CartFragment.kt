package com.example.tamangfood.presentation.ui.mainapp.home.cart

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentCartBinding
import com.example.tamangfood.domain.model.CartItem
import com.example.tamangfood.domain.model.CartSummary
import com.example.tamangfood.presentation.ui.mainapp.cart.CartAdapter
import com.example.tamangfood.presentation.ui.mainapp.home.HomeFragment
import com.example.tamangfood.presentation.ui.mainapp.home.HomeFragmentDirections
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayDeque
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<CartItem>()
    private var pendingDelete: PendingDelete? = null
    private val pendingQuantityUpdates = linkedMapOf<Int, Int>()
    private val quantityUpdateQueue = ArrayDeque<Pair<Int, Int>>()
    private var currentUpdatingItem: Pair<Int, Int>? = null
    private var shouldNavigateAfterSync = false
    private var isSyncingCart = false
    private var isLoadingCartItems = false
    private var serverTotalPrice: Int? = null

    private data class PendingDelete(
        val item: CartItem,
        val index: Int
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))

        setupRecyclerView()
        observeCartItems()
        observeDeleteCartItem()
        observeUpdateCartItemQuantity()
        checkEmptyListItem()
        calculateCount()
        setupClickListeners()
        updateUpdateCartButtonState()
        viewModel.getCartItems()
    }

    private fun checkEmptyListItem() {
        if (isLoadingCartItems) {
            binding.tvCartEmpty.visibility = View.GONE
            binding.cartScrollView.visibility = View.INVISIBLE
        } else if (cartItems.isEmpty()) {
            binding.tvCartEmpty.visibility = View.VISIBLE
            binding.cartScrollView.visibility = View.GONE
        } else {
            binding.tvCartEmpty.visibility = View.GONE
            binding.cartScrollView.visibility = View.VISIBLE
        }
    }

    private fun renderCartItemsLoading() {
        binding.progressCart.visibility = if (isLoadingCartItems) View.VISIBLE else View.GONE
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityChange = { item, newQuantity ->
                updateQuantity(item, newQuantity)
            },
            onItemClick = { selectedOrder ->
                val bundle = bundleOf(
                    "foodId" to selectedOrder.food.id
                )
                parentFragment
                    ?.findNavController()
                    ?.navigate(R.id.action_homeFragment_to_foodDetailFragment, bundle)

            }
        )

        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
        cartAdapter.submitList(cartItems.toList())

        attachSwipeToDelete()
    }

    private fun attachSwipeToDelete() {
        val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)
        val backgroundPaint = Paint().apply {
            color = Color.parseColor("#FFFFFFFF")
        }

        val touchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = cartAdapter.currentList[position]
                    removeItemOptimistically(item)
                    viewModel.deleteCartItem(item.id)
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                if (dX >= 0f) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    return
                }
                val height = itemView.bottom - itemView.top

                val background = RectF(
                    itemView.right + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )
                c.drawRect(background, backgroundPaint)

                deleteIcon?.let { icon ->
                    val scale = 2f
                    val iconWidth = (icon.intrinsicWidth * scale).toInt()
                    val iconHeight = (icon.intrinsicHeight * scale).toInt()

                    val iconMargin = (height - iconHeight) / 2
                    val iconTop = itemView.top + iconMargin
                    val iconLeft = itemView.right - iconMargin - iconWidth
                    val iconRight = itemView.right - iconMargin
                    val iconBottom = iconTop + iconHeight

                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    icon.draw(c)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        ItemTouchHelper(touchHelperCallback).attachToRecyclerView(binding.rvCartItems)
    }

    private fun updateQuantity(item: CartItem, newQuantity: Int) {
        val index = cartItems.indexOfFirst { it.id == item.id }
        if (index == -1) return

        val previousQuantity = cartItems[index].quantity
        if (previousQuantity == newQuantity) return

        cartItems[index] = cartItems[index].copy(quantity = newQuantity)
        serverTotalPrice = null
        pendingQuantityUpdates[item.id] = newQuantity
        cartAdapter.submitList(cartItems.toList())
        calculateCount()
        updateUpdateCartButtonState()
    }

    private fun removeItemOptimistically(item: CartItem) {
        val index = cartItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            pendingQuantityUpdates.remove(item.id)
            serverTotalPrice = null
            pendingDelete = PendingDelete(item = cartItems[index], index = index)
            cartItems.removeAt(index)
            cartAdapter.submitList(cartItems.toList())
            calculateCount()
            checkEmptyListItem()
            updateUpdateCartButtonState()
        }
    }

    private fun rollbackDeletedItemIfNeeded() {
        val pending = pendingDelete ?: return
        val restoreIndex = pending.index.coerceIn(0, cartItems.size)
        cartItems.add(restoreIndex, pending.item)
        cartAdapter.submitList(cartItems.toList())
        calculateCount()
        checkEmptyListItem()
        pendingDelete = null
    }

    private fun observeCartItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartItemsState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> {
                            isLoadingCartItems = true
                            renderCartItemsLoading()
                            checkEmptyListItem()
                        }
                        is NetworkState.Success<*> -> {
                            isLoadingCartItems = false
                            renderCartItemsLoading()
                            val summary = state.data as? CartSummary
                            val list = summary?.carts.orEmpty()
                            serverTotalPrice = summary?.totalPrice
                            cartItems.clear()
                            cartItems.addAll(list)
                            pendingQuantityUpdates.clear()
                            quantityUpdateQueue.clear()
                            currentUpdatingItem = null
                            shouldNavigateAfterSync = false
                            isSyncingCart = false
                            cartAdapter.submitList(cartItems.toList())
                            checkEmptyListItem()
                            calculateCount()
                            updateUpdateCartButtonState()
                        }
                        is NetworkState.Error -> {
                            isLoadingCartItems = false
                            renderCartItemsLoading()
                            checkEmptyListItem()
                            Utils.showToast(requireContext(), state.message)
                        }
                    }
                }
            }
        }
    }

    private fun observeDeleteCartItem() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteCartItemState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> Unit
                        is NetworkState.Success<*> -> {
                            pendingDelete = null
                            Utils.showToast(requireContext(), getString(R.string.cart_item_delete_success))
                            viewModel.resetDeleteCartItemState()
                        }
                        is NetworkState.Error -> {
                            rollbackDeletedItemIfNeeded()
                            Utils.showToast(requireContext(), state.message)
                            viewModel.resetDeleteCartItemState()
                        }
                    }
                }
            }
        }
    }

    private fun observeUpdateCartItemQuantity() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updateCartItemQuantityState.collect { state ->
                    when (state) {
                        is NetworkState.Init -> Unit
                        is NetworkState.Loading -> Unit
                        is NetworkState.Success<*> -> {
                            currentUpdatingItem?.let { (cartItemId, _) ->
                                pendingQuantityUpdates.remove(cartItemId)
                            }
                            currentUpdatingItem = null
                            viewModel.resetUpdateCartItemQuantityState()
                            updateUpdateCartButtonState()
                            processNextQuantityUpdate()
                        }
                        is NetworkState.Error -> {
                            isSyncingCart = false
                            shouldNavigateAfterSync = false
                            currentUpdatingItem = null
                            quantityUpdateQueue.clear()
                            updateUpdateCartButtonState()
                            Utils.showToast(requireContext(), state.message)
                            viewModel.resetUpdateCartItemQuantityState()
                        }
                    }
                }
            }
        }
    }

    private fun syncCartChanges(navigateAfterSync: Boolean) {
        if (pendingQuantityUpdates.isEmpty()) {
            if (navigateAfterSync) {
                proceedToCheckout()
            } else {
                Utils.showToast(requireContext(), getString(R.string.cart_no_pending_changes))
            }
            return
        }
        if (isSyncingCart) return

        shouldNavigateAfterSync = navigateAfterSync
        isSyncingCart = true
        quantityUpdateQueue.clear()
        quantityUpdateQueue.addAll(pendingQuantityUpdates.entries.map { it.key to it.value })
        updateUpdateCartButtonState()
        processNextQuantityUpdate()
    }

    private fun processNextQuantityUpdate() {
        if (!isSyncingCart || currentUpdatingItem != null) return

        val next = if (quantityUpdateQueue.isEmpty()) null else quantityUpdateQueue.removeFirst()
        if (next == null) {
            isSyncingCart = false
            val navigateAfterSync = shouldNavigateAfterSync
            shouldNavigateAfterSync = false
            updateUpdateCartButtonState()
            Utils.showToast(requireContext(), getString(R.string.cart_updated_success))
            if (navigateAfterSync) proceedToCheckout()
            return
        }

        currentUpdatingItem = next
        val (cartItemId, quantity) = next
        viewModel.updateCartItemQuantity(cartItemId, quantity)
    }

    private fun updateUpdateCartButtonState() {
        val hasPendingChanges = pendingQuantityUpdates.isNotEmpty()
        binding.btnUpdateCart.isEnabled = hasPendingChanges && !isSyncingCart
        binding.btnCheckout.isEnabled = !isSyncingCart
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            // TODO: confirm to close (don't save this changes)
            closeDrawer()
        }

        binding.btnUpdateCart.setOnClickListener {
            syncCartChanges(navigateAfterSync = false)
        }

        binding.btnCheckout.setOnClickListener {
            syncCartChanges(navigateAfterSync = true)
        }
    }

    private fun proceedToCheckout() {
        checkBottomNav()
        closeDrawer()
        Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
        parentFragment?.findNavController()?.navigate(HomeFragmentDirections.actionHomeFragmentToConfirmOrderFragment())
    }

    private fun closeDrawer() {
        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.fragment_home)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        drawerLayout.closeDrawer(GravityCompat.END)
    }
    private fun checkBottomNav(){
        (parentFragment as? HomeFragment)?.isNavigatingToFragment = true
    }

    private fun calculateCount() {
        val count = cartItems.sumOf { it.quantity }
        binding.tvCartItemsCount.text = getString(R.string.you_have_items_in_cart, count)

        val shouldUseServerTotal = !isSyncingCart && pendingQuantityUpdates.isEmpty() && pendingDelete == null
        val totalAmount = if (shouldUseServerTotal && serverTotalPrice != null) {
            serverTotalPrice!!.toDouble()
        } else {
            cartItems.sumOf { item ->
                val ingredientUnitPrice = item.ingredients.sumOf { it.price }
                val unitPrice = item.food.price + ingredientUnitPrice
                unitPrice * item.quantity
            }.toDouble()
        }
        binding.tvTotal.text = String.format("$%.2f", totalAmount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
