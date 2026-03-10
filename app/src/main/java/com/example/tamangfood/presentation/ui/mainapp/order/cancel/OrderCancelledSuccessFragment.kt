package com.example.tamangfood.presentation.ui.mainapp.order.cancel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentOrderCancelledSuccessBinding
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderCancelledSuccessFragment : Fragment() {
    private var _binding: FragmentOrderCancelledSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderCancelledSuccessBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))

        // Navigate back to orders after a delay or on back press
        view.postDelayed({
            if (isAdded) {
                findNavController().popBackStack(R.id.orderFragment, false)
            }
        }, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}