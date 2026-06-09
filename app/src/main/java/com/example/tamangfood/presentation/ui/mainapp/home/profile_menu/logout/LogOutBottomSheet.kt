package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.logout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.R
import com.example.tamangfood.databinding.BottomSheetLogOutBinding
import com.example.tamangfood.presentation.utils.AppPreferences
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LogOutBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetLogOutBinding? = null
    private val binding get() = _binding!!


    companion object {
        const val TAG = "AddressFormBottomSheet"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetLogOutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClickListeners()
    }

    override fun onStart() {
        super.onStart()

        dialog?.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        )?.setBackgroundResource(android.R.color.transparent)
    }

    private fun setClickListeners() {
        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnLogOut.setOnClickListener {
            AppPreferences.clearSession()
            dismiss()

            val navController = findNavController()

            navController.setGraph(R.navigation.auth_navigation)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}