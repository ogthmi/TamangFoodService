package com.example.tamangfood.presentation.ui.mainapp.home.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentFilterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterFragment : Fragment(R.layout.fragment_filter) {
    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!

    private lateinit var subCategoryAdapter: SubCategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupRecyclerView()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private val categories = listOf(
        Category(R.drawable.ic_snack, "Snacks"),
        Category(R.drawable.ic_meal, "Meal"),
        Category(R.drawable.ic_vegan, "Vegan"),
        Category(R.drawable.ic_dessert, "Dessert"),
        Category(R.drawable.ic_drink, "Drinks"),
    )

    private val subCategoryMap = mapOf(
        "Snacks" to listOf("Bruschetta", "Spring Rolls", "Crepes", "Wings", "Skewers", "Salmon", "Mexican", "Baked", "Appetizer"),
        "Meal" to listOf("Sushi", "Pizza", "Chicken", "Curry", "Burger", "Cheese", "Fresh Prawn", "Ceviche", "Pad Thai"),
        "Vegan" to listOf("Bean Burger", "Risotto", "Broccoli", "Lasagna", "Pizza", "Mushroom", "Hummus", "Quinoa", "Salad"),
        "Dessert" to listOf("Crepes", "Macarons", "Cupcakes", "Ice Cream", "Flan", "Cheesecake", "Chocolate", "Cakes", "Brownie"),
        "Drinks" to listOf("Coffee", "Cocktail", "Juice", "Milkshake", "Wine", "Pina Colada","Mojito", "Craft Beer", "Ice Tea")
    )

    private fun setupRecyclerView() {
        subCategoryAdapter = SubCategoryAdapter(
            subCategoryMap[categories.first().name] ?: emptyList()
        ) { subCategory -> // TODO: handle on click subcategory

        }
        binding.rvSubCategories.adapter = subCategoryAdapter
        binding.rvSubCategories.layoutManager = GridLayoutManager(requireContext(), 3)

        val adapter = CategoryAdapter(categories) { category ->
            val subCategories = subCategoryMap[category.name] ?: emptyList()
            subCategoryAdapter.updateData(subCategories)

        }

        binding.rvCategories.adapter = adapter
        binding.rvCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

}