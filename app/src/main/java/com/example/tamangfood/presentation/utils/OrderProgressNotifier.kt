package com.example.tamangfood.presentation.utils

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object OrderProgressNotifier {

    fun schedule(context: Context, orderId: Int) {
        enqueue(
            context = context,
            orderId = orderId,
            delaySeconds = 30,
            notificationId = orderId * 10 + 1,
            title = "Your order is being prepared",
            message = "The restaurant has started preparing your order."
        )
        enqueue(
            context = context,
            orderId = orderId,
            delaySeconds = 60,
            notificationId = orderId * 10 + 2,
            title = "Your order is on the way",
            message = "The driver is heading to your location. Tap to track your order.",
            shouldOpenTracking = true
        )
    }

    fun notifyArrived(context: Context, orderId: Int) {
        enqueue(
            context = context,
            orderId = orderId,
            delaySeconds = 0,
            notificationId = orderId * 10 + 3,
            title = "Your order has arrived",
            message = "Your order has arrived. Please come and receive it.",
            shouldMarkOrderCompleted = true
        )
    }

    private fun enqueue(
        context: Context,
        orderId: Int,
        delaySeconds: Long,
        notificationId: Int,
        title: String,
        message: String,
        shouldOpenTracking: Boolean = false,
        shouldMarkOrderCompleted: Boolean = false
    ) {
        val inputData = Data.Builder()
            .putString(OrderProgressNotificationWorker.KEY_TITLE, title)
            .putString(OrderProgressNotificationWorker.KEY_MESSAGE, message)
            .putInt(OrderProgressNotificationWorker.KEY_NOTIFICATION_ID, notificationId)
            .putInt(OrderProgressNotificationWorker.KEY_ORDER_ID, orderId)
            .putBoolean(OrderProgressNotificationWorker.KEY_OPEN_TRACKING, shouldOpenTracking)
            .putBoolean(
                OrderProgressNotificationWorker.KEY_MARK_ORDER_COMPLETED,
                shouldMarkOrderCompleted
            )
            .build()

        val request = OneTimeWorkRequestBuilder<OrderProgressNotificationWorker>()
            .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
            .setInputData(inputData)
            .addTag("order_progress_$orderId")
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }
}
