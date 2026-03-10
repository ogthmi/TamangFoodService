package com.example.tamangfood.presentation.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.tamangfood.R
import com.google.android.material.bottomnavigation.BottomNavigationView

object Utils {
    fun showToast(context: Context, message: String){
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun showBottomNav(bottomNav: CardView){
        bottomNav.visibility = View.VISIBLE
    }

    fun hideBottomNav(bottomNav: CardView){
        bottomNav.visibility = View.GONE
    }

}

enum class DefaultLocation(val value: Double){ //Set HaNoi
    LAT(21.0278),
    LNG(105.8342)
}

enum class Zoom(val value: Double){
    DEFAULT(15.0),
}

enum class OrderStatus {
    ACTIVE,
    COMPLETED,
    CANCELLED
}

enum class FoodType {
    SNACK,
    MEAL,
    VEGAN,
    DESSERT,
    DRINK
}

enum class ImageFoodType(val imageRes: Int){
    SNACK(R.drawable.ic_snack),
    MEAL(R.drawable.ic_meal),
    VEGAN(R.drawable.ic_vegan),
    DESSERT(R.drawable.ic_dessert),
    DRINK(R.drawable.ic_drink)
}

