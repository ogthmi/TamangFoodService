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
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tamangfood.R
import com.example.tamangfood.data.model.CartItem
import com.example.tamangfood.data.model.Food
import com.example.tamangfood.databinding.FragmentCartBinding
import com.example.tamangfood.presentation.ui.mainapp.cart.CartAdapter
import com.example.tamangfood.presentation.utils.FoodType
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<CartItem>()

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
        Utils.showBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))

        loadMockData()
        setupRecyclerView()
        checkEmptyListItem()
        calculateCount()
        setupClickListeners()
    }

    private fun checkEmptyListItem() {
        if (cartItems.isEmpty()) {
            binding.tvCartEmpty.visibility = View.VISIBLE
            binding.cartScrollView.visibility = View.GONE
        } else {
            binding.tvCartEmpty.visibility = View.GONE
            binding.cartScrollView.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onQuantityChange = { item, newQuantity ->
                updateQuantity(item, newQuantity)
            },
            onItemClick = {
                // TODO: navigate to detail item
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
                    removeItem(item)
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
                    val scale = 4f
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
        if (index != -1) {
            cartItems[index] = cartItems[index].copy(quantity = newQuantity)
            cartAdapter.submitList(cartItems.toList())
            calculateCount()
        }
    }

    private fun removeItem(item: CartItem) {
        val index = cartItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            cartItems.removeAt(index)
            cartAdapter.submitList(cartItems.toList())
            calculateCount()
            checkEmptyListItem()
        }
    }

    private fun loadMockData() {
        // TODO: call api get data
        cartItems.clear()
        cartItems.addAll(
            listOf(
                CartItem(
                    id = 1,
                    food = Food(
                        id = 1,
                        name = "Strawberry Shake",
                        price = "$20.00",
                        quantity = 5,
                        rating = 4.5,
                        type = FoodType.DESSERT,
                        imageRes = R.drawable.ic_launcher_background
                    ),
                    quantity = 2,
                    dateTime = "29/11/24 15:00"
                ),
                CartItem(
                    id = 2,
                    food = Food(
                        id = 2,
                        name = "Broccoli Lasagna",
                        price = "$12.00",
                        quantity = 3,
                        rating = 4.0,
                        type = FoodType.MEAL,
                        imageRes = R.drawable.ic_launcher_background
                    ),
                    quantity = 1,
                    dateTime = "29/11/24 12:00"
                )
            )
        )
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            // TODO: confirm to close (don't save this changes)
            closeDrawer()
        }
    }

    private fun closeDrawer() {
        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.fragment_home)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        drawerLayout.closeDrawer(GravityCompat.END)
    }

    private fun calculateCount() {
        val count = cartItems.sumOf { it.quantity }
        binding.tvCartItemsCount.text = getString(R.string.you_have_items_in_cart, count)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
