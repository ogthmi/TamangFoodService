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
import com.example.tamangfood.presentation.utils.FoodCategoryProvider
import com.example.tamangfood.presentation.utils.FoodType
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterFragment : Fragment(R.layout.fragment_filter) {
    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!

    private lateinit var foodTypeAdapter: FoodTypeAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    private val foodTypes = FoodType.entries
    private val categoryProvider = FoodCategoryProvider()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter { category ->
            // TODO: handle category click
        }

        binding.rvCategories.apply {
            adapter = categoryAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            itemAnimator = null
        }

        foodTypeAdapter = FoodTypeAdapter { foodType ->
            val categories = categoryProvider.getCategories(foodType)
            categoryAdapter.clearSelection()
            categoryAdapter.submitList(categories)
        }

        binding.rvFoodTypes.apply {
            adapter = foodTypeAdapter

            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )
        }

        foodTypeAdapter.submitList(foodTypes.toList())

        val firstCategoryList = categoryProvider.getCategories(foodTypes.first())
        categoryAdapter.submitList(firstCategoryList)
    }

}