package com.example.tamangfood.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.example.tamangfood.R
import com.example.tamangfood.databinding.ActivityMainBinding
import com.example.tamangfood.presentation.utils.AppPreferences
import com.example.tamangfood.presentation.utils.SessionExpiredBus
import com.example.tamangfood.presentation.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupStartDestination()
        observeSessionExpired()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        if (AppPreferences.consumeSessionExpired()) {
            enforceLogoutIfTokenExpiredByApi()
        }
    }

    private fun setupStartDestination() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerViewOnboarding) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.welcome_navigation)

        navGraph.setStartDestination(
            if (AppPreferences.hasSession()) R.id.mainAppFragment2 else R.id.onboardingFragment
        )

        navController.graph = navGraph
    }

    private fun enforceLogoutIfTokenExpiredByApi() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerViewOnboarding) as? NavHostFragment ?: return
        val navController = navHostFragment.navController
        if (navController.currentDestination?.id == R.id.authenticationFragment) return
        navController.navigate(
            R.id.authenticationFragment,
            null,
            navOptions {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        )
        Utils.showToast(this, "Your login session has expired. Please log in again!")
    }

    private fun observeSessionExpired() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                SessionExpiredBus.events.collect {
                    enforceLogoutIfTokenExpiredByApi()
                }
            }
        }
    }
}