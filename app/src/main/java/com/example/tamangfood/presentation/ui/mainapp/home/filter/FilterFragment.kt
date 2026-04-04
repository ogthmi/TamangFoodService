package com.example.tamangfood.presentation.ui.mainapp.home.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentFilterBinding
import com.example.tamangfood.domain.model.FoodCategory
import com.example.tamangfood.domain.model.FoodCategoryDetail
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilterFragment : Fragment(R.layout.fragment_filter) {
    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FilterViewModel by viewModels()

    private lateinit var foodTypeAdapter: FoodTypeAdapter
    private lateinit var categoryAdapter: CategoryAdapter

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
        observeCategoriesState()
        observeDetailsState()
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
        categoryAdapter = CategoryAdapter { /* TODO: apply filter selection */ }

        binding.rvCategories.apply {
            adapter = categoryAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            itemAnimator = null
        }

        foodTypeAdapter = FoodTypeAdapter { category ->
            viewModel.loadCategoryDetails(category.id)
        }

        binding.rvFoodTypes.apply {
            adapter = foodTypeAdapter
            layoutManager = LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false
            )
        }
    }

    private fun observeCategoriesState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoriesState.collect { state ->
                    when (state) {
                        is NetworkState.Loading, NetworkState.Init -> {
                            binding.progressBarType.visibility = View.VISIBLE
                            viewModel.resetDetailsState()
                        }
                        is NetworkState.Success<*> -> {
                            binding.progressBarType.visibility = View.GONE
                            @Suppress("UNCHECKED_CAST")
                            val raw = state.data as? List<FoodCategory> ?: emptyList()
                            val ordered = raw.sortedWith(
                                compareBy<FoodCategory> { it.type.ordinal }.thenBy { it.name }
                            )
                            foodTypeAdapter.submitList(ordered)
                            binding.rvFoodTypes.post {
                                foodTypeAdapter.resetSelectionToFirst()
                            }
                            if (ordered.isEmpty()) {
                                viewModel.resetDetailsState()
                                applyDetailsUi(NetworkState.Init)
                            } else {
                                viewModel.loadCategoryDetails(ordered.first().id)
                            }
                        }
                        is NetworkState.Error -> {
                            binding.progressBarType.visibility = View.GONE
                            foodTypeAdapter.submitList(emptyList())
                            viewModel.resetDetailsState()
                            Utils.showToast(requireContext(), state.message)
                        }
                    }
                }
            }
        }
    }

    private fun observeDetailsState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.detailsState.collect { state ->
                    applyDetailsUi(state)
                }
            }
        }
    }

    private fun applyDetailsUi(state: NetworkState) {
        when (state) {
            is NetworkState.Init -> {
                binding.progressBarDetails.visibility = View.GONE
                categoryAdapter.clearSelection()
                categoryAdapter.submitList(emptyList())
            }
            is NetworkState.Loading -> {
                binding.progressBarDetails.visibility = View.VISIBLE
            }
            is NetworkState.Success<*> -> {
                binding.progressBarDetails.visibility = View.GONE
                categoryAdapter.clearSelection()
                @Suppress("UNCHECKED_CAST")
                val list = state.data as? List<FoodCategoryDetail> ?: emptyList()
                categoryAdapter.submitList(list)
            }
            is NetworkState.Error -> {
                binding.progressBarDetails.visibility = View.GONE
                categoryAdapter.clearSelection()
                categoryAdapter.submitList(emptyList())
                Utils.showToast(requireContext(), state.message)
            }
        }
    }
}
