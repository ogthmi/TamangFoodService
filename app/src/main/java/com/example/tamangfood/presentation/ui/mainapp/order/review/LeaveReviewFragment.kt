package com.example.tamangfood.presentation.ui.mainapp.order.review

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
import androidx.navigation.fragment.navArgs
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentLeaveReviewBinding
import com.example.tamangfood.presentation.utils.ImageLoader
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LeaveReviewFragment : Fragment() {
    private var _binding: FragmentLeaveReviewBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LeaveReviewViewModel by viewModels()
    private val args: LeaveReviewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaveReviewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))

        bindArgs()
        setupClickListeners()
        observeViewModel()
    }

    private fun bindArgs() {
        binding.tvFoodName.text = args.foodName.ifBlank { "Order #${args.orderId}" }
        if (args.foodImage.isNotBlank()) {
            ImageLoader.load(requireContext(), binding.ivFoodImage, args.foodImage)
        } else {
            binding.ivFoodImage.setImageResource(R.drawable.ic_tamang_logo)
        }
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSubmit.setOnClickListener {
            val rating = binding.llRatingStars.rating.toDouble()
            val comment = binding.etReviewComment.text.toString().trim()

            if (rating == 0.0) {
                Utils.showToast(requireContext(), "Please select a rating")
                return@setOnClickListener
            }
            if (comment.isBlank()) {
                Utils.showToast(requireContext(), "Please write a comment")
                return@setOnClickListener
            }

            viewModel.submitReview(
                orderId = args.orderId,
                foodId = args.foodId,
                rating = rating,
                comment = comment
            )
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { loading ->
                binding.btnSubmit.isEnabled = !loading
                binding.btnCancel.isEnabled = !loading
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.submitResult.collect { state ->
                    when (state) {
                        is NetworkState.Success<*> -> {
                            Utils.showToast(requireContext(), "Review submitted successfully!")
                            try {
                                findNavController().getBackStackEntry(R.id.foodDetailFragment)
                                    .savedStateHandle
                                    .set("comment_submitted", true)
                            } catch (_: Exception) {
                            }
                            findNavController().popBackStack()
                        }
                        is NetworkState.Error -> {
                            Utils.showToast(requireContext(), state.message)
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
