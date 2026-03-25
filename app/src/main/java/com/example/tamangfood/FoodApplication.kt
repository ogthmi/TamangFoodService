package com.example.tamangfood

import android.app.Application
import com.example.tamangfood.presentation.utils.AppPreferences
import com.stripe.android.PaymentConfiguration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FoodApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val key = BuildConfig.STRIPE_PUBLISHABLE_KEY
        PaymentConfiguration.init(this, key)
        AppPreferences.init(context = this)
    }
}