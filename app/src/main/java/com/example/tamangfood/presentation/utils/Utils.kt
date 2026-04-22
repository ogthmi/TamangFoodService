package com.example.tamangfood.presentation.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.tamangfood.R

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

enum class FoodType(val imageRes: Int, val title: Int, val tabSelector: Int){
    SNACK(R.drawable.ic_snack, R.string.snacks, R.drawable.tab_snacks_selector),
    MEAL(R.drawable.ic_meal, R.string.meal, R.drawable.tab_meal_selector),
    VEGAN(R.drawable.ic_vegan, R.string.vegan, R.drawable.tab_vegan_selector),
    DESSERT(R.drawable.ic_dessert, R.string.dessert, R.drawable.tab_dessert_selector),
    DRINK(R.drawable.ic_drink, R.string.drinks, R.drawable.tab_drinks_selector);
}

/** Maps API category name to local enum for icons; falls back to [FoodType.MEAL] when unknown. */
fun String.toFoodTypeOrDefault(): FoodType {
    val compact = trim().lowercase().replace(Regex("[\\s_-]+"), "")
    val matched = when {
        "snack" in compact -> FoodType.SNACK
        "meal" in compact -> FoodType.MEAL
        "vegan" in compact -> FoodType.VEGAN
        "dessert" in compact -> FoodType.DESSERT
        "drink" in compact -> FoodType.DRINK
        else -> FoodType.entries.firstOrNull {
            it.name.equals(trim().replace(' ', '_'), ignoreCase = true)
        }
    }
    return matched ?: FoodType.MEAL
}

class FoodCategoryProvider {
    private val categoryMap = mapOf(
        FoodType.SNACK to listOf("Bruschetta","Spring Rolls","Crepes","Wings","Skewers","Salmon","Mexican","Baked","Appetizer"),
        FoodType.MEAL to listOf("Sushi","Pizza","Chicken","Curry","Burger","Cheese","Fresh Prawn","Ceviche","Pad Thai"),
        FoodType.VEGAN to listOf("Bean Burger","Risotto","Broccoli","Lasagna","Pizza","Mushroom","Hummus","Quinoa","Salad"),
        FoodType.DESSERT to listOf("Crepes","Macarons","Cupcakes","Ice Cream","Flan","Cheesecake","Chocolate","Cakes","Brownie"),
        FoodType.DRINK to listOf("Coffee","Cocktail","Juice","Milkshake","Wine","Pina Colada","Mojito","Craft Beer","Ice Tea")
    )

    fun getCategories(foodType: FoodType): List<String> {
        return categoryMap[foodType] ?: emptyList()
    }
}

enum class HTTP(val status: Int){
    SUCCESS(200),
    CREATED(201),
}
