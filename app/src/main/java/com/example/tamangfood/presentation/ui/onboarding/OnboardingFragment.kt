package com.example.tamangfood.presentation.ui.onboarding

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.tamangfood.R
import com.example.tamangfood.databinding.FragmentOnboardingBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : Fragment(R.layout.fragment_onboarding) {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: OnboardingAdapter

    private val items = listOf(
        OnboardingItem(
            R.drawable.img_welcome,
            "Welcome",
            "It’s a pleasure to meet you. Let’s get started!"
        ),
        OnboardingItem(
            R.drawable.img_onboarding_1,
            "All your favorites",
            "Order from the best local restaurants with easy, on-demand delivery"
        ),
        OnboardingItem(
            R.drawable.img_onboarding_2,
            "Free delivery offers",
            "Free delivery for new customers via Apple Pay and others payment methods"
        ),
        OnboardingItem(
            R.drawable.img_onboarding_3,
            "Choose your food",
            "Easily find your type of food craving and you’ll get delivery in wide range"
        )
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentOnboardingBinding.bind(view)

        setupViewPager()
        setupIndicator()
        setupClick()
    }

    private fun setupViewPager() {
        adapter = OnboardingAdapter(items)

        binding.viewPager.adapter = adapter

        binding.viewPager.offscreenPageLimit = items.size
    }

    private fun setupIndicator() {

        TabLayoutMediator(binding.tabIndicator, binding.viewPager) { _, _ ->

        }.attach()

        val tabStrip = binding.tabIndicator.getChildAt(0) as ViewGroup

        for (i in 0 until tabStrip.childCount) {

            val tabView = tabStrip.getChildAt(i)

            val params = tabView.layoutParams as ViewGroup.MarginLayoutParams
            params.marginStart = 12
            params.marginEnd = 12

            tabView.layoutParams = params
        }
    }

    private fun setupClick() {
        binding.btnGetStarted.setOnClickListener {
            findNavController().navigate(
                OnboardingFragmentDirections.actionOnboardingFragmentToMainAppFragment2(),
                navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.onboardingFragment, true)
                    .build()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}