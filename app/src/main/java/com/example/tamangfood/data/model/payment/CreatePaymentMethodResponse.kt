package com.example.tamangfood.data.model.payment

import com.example.tamangfood.domain.model.Card
import com.google.gson.annotations.SerializedName

data class CreatePaymentMethodResponse(
    val code: Int,
    val message: String,
    val result: PaymentMethod?
)

data class PaymentMethod(
    val id: String?,
    val userId: Int?,
    val holderName: String?,
    val cardBrand: String?,
    val last4: String?,
    @SerializedName("exp_month") val expMonth: String?,
    @SerializedName("exp_year") val expYear: String?
)

fun PaymentMethod.toDomain(): Card {
    return Card(
        paymentMethodId = id.orEmpty(),
        brand = cardBrand.orEmpty(),
        last4 = last4.orEmpty(),
        expMonth = expMonth.orEmpty(),
        expYear = expYear.orEmpty()
    )
}