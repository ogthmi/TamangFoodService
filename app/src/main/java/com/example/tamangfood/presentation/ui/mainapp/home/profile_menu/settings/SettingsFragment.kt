package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.settings

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentSettingsBinding
import com.example.tamangfood.presentation.utils.AppPreferences
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
        viewModel.resetDeleteAccountState()
        observeDeleteAccount()
        setupClickListeners()
    }

    private fun observeDeleteAccount() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deleteAccountState.collect { state ->
                when (state) {
                    is NetworkState.Init -> Unit
                    is NetworkState.Loading -> {
                        binding.deleteAccountLayout.isEnabled = false
                    }
                    is NetworkState.Success<*> -> {
                        binding.deleteAccountLayout.isEnabled = true
                        AppPreferences.clearSession()
                        Utils.showToast(requireContext(), getString(R.string.delete_account_success))
                        viewModel.resetDeleteAccountState()
                        findNavController().setGraph(R.navigation.auth_navigation)
                    }
                    is NetworkState.Error -> {
                        binding.deleteAccountLayout.isEnabled = true
                        Utils.showToast(requireContext(), state.message)
                        viewModel.resetDeleteAccountState()
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_homeFragment)
        }

        binding.passwordSettingLayout.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_passwordSettingFragment)
        }

        binding.deleteAccountLayout.setOnClickListener {
            showDeleteAccountDialog()
        }
    }

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_account_dialog_title)
            .setMessage(R.string.delete_account_dialog_message)
            .setNegativeButton(R.string.delete_account_cancel) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.delete_account_confirm) { dialog, _ ->
                dialog.dismiss()
                val userId = AppPreferences.getUserId() ?: -1
                if (userId < 0) {
                    Utils.showToast(requireContext(), getString(R.string.delete_account_invalid_user))
                    return@setPositiveButton
                }
                viewModel.deleteAccount(userId)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
