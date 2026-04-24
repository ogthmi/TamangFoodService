package com.example.tamangfood.presentation.ui.mainapp.order.cancel

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tamangfood.R
import com.example.tamangfood.data.model.CancelReason
import com.example.tamangfood.databinding.FragmentCancelOrderBinding
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CancelOrderFragment : Fragment() {
    private var _binding: FragmentCancelOrderBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CancelOrderViewModel by viewModels()
    private val args: CancelOrderFragmentArgs by navArgs()
    private lateinit var cancelReasonAdapter: CancelReasonAdapter

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

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        cancelReasonAdapter = CancelReasonAdapter(onReasonSelected = { })
        binding.rvCancelReasons.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cancelReasonAdapter
        }
        cancelReasonAdapter.submitList(
            listOf(
                CancelReason(1, "I ordered by mistake"),
                CancelReason(2, "The delivery time is too long"),
                CancelReason(3, "I found a better price elsewhere"),
                CancelReason(4, "The restaurant is too far away"),
                CancelReason(5, "I changed my mind"),
                CancelReason(6, "Others")
            )
        )
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSubmit.setOnClickListener {
            viewModel.cancelOrder(args.orderId)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { loading ->
                binding.btnSubmit.isEnabled = !loading
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cancelResult.collect { state ->
                    when (state) {
                        is NetworkState.Success<*> -> {
                            findNavController().previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("order_cancelled", true)
                            findNavController().navigate(
                                CancelOrderFragmentDirections
                                    .actionCancelOrderFragmentToOrderCancelledSuccessFragment(
                                        true,
                                        false
                                    )
                            )
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
