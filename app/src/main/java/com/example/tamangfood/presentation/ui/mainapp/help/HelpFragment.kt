package com.example.tamangfood.presentation.ui.mainapp.help

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentHelpBinding
import com.example.tamangfood.domain.model.Sample
import com.example.tamangfood.presentation.utils.NetworkState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HelpFragment : Fragment() {
    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HelpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        executeSample()
    }

    private fun executeSample(){
        viewModel.sample()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sampleState.collect{state ->
                when (state) {
                    is NetworkState.Init, NetworkState.Loading -> {
                        // Example: Visible Loading progress
                    }
                    is NetworkState.Success<*> -> {

                    }
                    is NetworkState.Error -> {

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