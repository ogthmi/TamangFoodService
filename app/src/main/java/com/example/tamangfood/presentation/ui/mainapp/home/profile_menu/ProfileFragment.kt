package com.example.tamangfood.presentation.ui.mainapp.home.profile_menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.R
import com.example.tamangfood.data.model.user.profile.UserProfileResponse
import com.example.tamangfood.databinding.FragmentProfileBinding
import com.example.tamangfood.presentation.ui.mainapp.home.HomeFragment
import com.example.tamangfood.presentation.ui.mainapp.home.HomeFragmentDirections
import com.example.tamangfood.presentation.ui.mainapp.home.profile_menu.logout.LogOutBottomSheet
import com.example.tamangfood.presentation.utils.AppPreferences
import com.example.tamangfood.presentation.utils.ImageLoader
import com.example.tamangfood.presentation.utils.NetworkState
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels();

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId: Int = AppPreferences.getUserId() ?: -1
        viewModel.getUserProfile(userId)

        observeViewModel()
        setupClickListeners()
    }

    fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profileState.collect { state ->
                when (state) {
                    is NetworkState.Init, NetworkState.Loading -> Unit

                    is NetworkState.Success<*> -> {
                        val response = state.data as UserProfileResponse

                        binding.apply {
                            tvEmail.text = response.result.email
                            tvFullName.text = response.result.fullName
                        }
                        ImageLoader.load(requireContext(), binding.ivAvt, response.result.imageUrl)
                    }

                    is NetworkState.Error -> {
                        Utils.showToast(requireContext(), state.message)
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.menuMyProfile.setOnClickListener {
            checkBottomNav()
            closeDrawer()
            Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
            parentFragment?.findNavController()
                ?.navigate(HomeFragmentDirections.actionHomeFragmentToUpdateMyProfileFragment())
        }

        binding.menuMyOrders.setOnClickListener {
            checkBottomNav()
            closeDrawer()
            Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
            val action = HomeFragmentDirections.actionHomeFragmentToOrderFragment()
            action.isFromDrawer = true
            parentFragment?.findNavController()?.navigate(action)
        }


        binding.menuDeliveryAddress.setOnClickListener {
            checkBottomNav()
            closeDrawer()
            Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
            parentFragment?.findNavController()
                ?.navigate(HomeFragmentDirections.actionHomeFragmentToDeliveryAddressFragment())
        }

        binding.menuPaymentMethods.setOnClickListener {
            checkBottomNav()
            closeDrawer()
            Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
            parentFragment?.findNavController()?.navigate(
                HomeFragmentDirections.actionHomeFragmentToPaymentMethodsFragment()
            )
        }

        binding.menuSetting.setOnClickListener {
            checkBottomNav()
            closeDrawer()
            Utils.hideBottomNav(requireActivity().findViewById(R.id.bottom_nav_layout))
            parentFragment?.findNavController()?.navigate(
                HomeFragmentDirections.actionHomeFragmentToSettingsFragment()
            )
        }

        binding.menuLogout.setOnClickListener {
            checkBottomNav()
            val bottomSheet = LogOutBottomSheet()
            bottomSheet.show(parentFragmentManager, LogOutBottomSheet.TAG)
//            closeDrawer()
        }
    }

    private fun closeDrawer() {
        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.fragment_home)
        drawerLayout.closeDrawer(GravityCompat.END)
    }

    private fun checkBottomNav() {
        (parentFragment as? HomeFragment)?.isNavigatingToFragment = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}