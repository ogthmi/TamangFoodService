package com.example.tamangfood.presentation.ui.mainapp.order.cancel

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentOrderCancelledSuccessBinding
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderCancelledSuccessFragment : Fragment() {
    private var _binding: FragmentOrderCancelledSuccessBinding? = null
    private val binding get() = _binding!!
    private val args: OrderCancelledSuccessFragmentArgs by navArgs()

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

        val isFromCancelOrder = args.isFromCancelOrder
        val isFromPlaceOrder = args.isFromPlaceOrder

        if(isFromCancelOrder){
            binding.tvSuccessTitle.text = getString(R.string.order_cancelled_title)
            binding.tvSuccessMessage.text = getString(R.string.order_cancelled_message)
            binding.tvTrackOrder.visibility = View.GONE
            binding.btnClose.visibility = View.GONE
            // Navigate back to orders after a delay or on back press
            view.postDelayed({
                if (isAdded) {
                    findNavController().popBackStack(R.id.orderFragment, false)
                }
            }, 2000)
        }
        else if(isFromPlaceOrder){
            binding.tvSuccessTitle.text = getString(R.string.place_order_title)
            binding.tvSuccessMessage.text = getString(R.string.place_order_message)
            binding.tvTrackOrder.visibility = View.VISIBLE
            binding.btnClose.visibility = View.VISIBLE
            binding.tvTrackOrder.paintFlags =
                binding.tvTrackOrder.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            binding.tvTrackOrder.setOnClickListener {
                // TODO: Track driver
            }

            binding.btnClose.setOnClickListener {
                findNavController().popBackStack()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}