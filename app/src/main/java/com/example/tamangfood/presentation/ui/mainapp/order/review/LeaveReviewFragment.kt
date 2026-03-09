package com.example.tamangfood.presentation.ui.mainapp.order.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentLeaveReviewBinding
import com.example.tamangfood.presentation.ui.mainapp.order.review.LeaveReviewViewModel
import com.example.tamangfood.presentation.ui.mainapp.order.OrderViewModel
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LeaveReviewFragment : Fragment() {
    private var _binding: FragmentLeaveReviewBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LeaveReviewViewModel by viewModels()
    private val orderViewModel: OrderViewModel by activityViewModels()
    private val args: LeaveReviewFragmentArgs by navArgs()
    private var selectedRating = 0

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

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSubmit.setOnClickListener {
           findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}