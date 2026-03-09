package com.example.tamangfood.presentation.ui.mainapp.order.cancel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.data.model.CancelReason
import com.example.tamangfood.databinding.FragmentCancelOrderBinding
import com.example.tamangfood.presentation.ui.mainapp.order.OrderViewModel
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CancelOrderFragment : Fragment() {
    private var _binding: FragmentCancelOrderBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CancelOrderViewModel by viewModels()
    private val orderViewModel: OrderViewModel by activityViewModels()
    private val args: CancelOrderFragmentArgs by navArgs()
    private lateinit var cancelReasonAdapter: CancelReasonAdapter
    private lateinit var reasonCancel: List<CancelReason>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCancelOrderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))

        mockData()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {

        cancelReasonAdapter = CancelReasonAdapter(
            onReasonSelected = { }
        )
        binding.rvCancelReasons.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cancelReasonAdapter
        }
        cancelReasonAdapter.submitList(reasonCancel)
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSubmit.setOnClickListener {
            val selectedReason = cancelReasonAdapter.getSelectedReason()
            findNavController().navigate(CancelOrderFragmentDirections.actionCancelOrderFragmentToOrderCancelledSuccessFragment())
        }

    }

    private fun observeViewModel() {

    }

    private fun mockData(){
        reasonCancel = listOf(
            CancelReason(1, "I ordered by mistake"),
            CancelReason(2, "The delivery time is too long"),
            CancelReason(3, "I found a better price elsewhere"),
            CancelReason(4, "The restaurant is too far away"),
            CancelReason(5, "I changed my mind"),
            CancelReason(6, "Others")
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}