package com.example.tamangfood.presentation.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.tamangfood.BuildConfig
import com.example.tamangfood.R
import com.example.tamangfood.data.api.ApiService
import com.example.tamangfood.data.model.order.UpdateOrderStatusRequest
import com.example.tamangfood.presentation.MainActivity
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OrderProgressNotificationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE).orEmpty()
        val message = inputData.getString(KEY_MESSAGE).orEmpty()
        val notificationId = inputData.getInt(KEY_NOTIFICATION_ID, 0)
        val orderId = inputData.getInt(KEY_ORDER_ID, -1)
        val shouldOpenTracking = inputData.getBoolean(KEY_OPEN_TRACKING, false)
        val shouldMarkOrderCompleted = inputData.getBoolean(KEY_MARK_ORDER_COMPLETED, false)

        if (shouldMarkOrderCompleted && orderId > 0) {
            updateOrderStatusToCompleted(orderId)
        }

        createChannelIfNeeded()

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (shouldOpenTracking) {
                putExtra(EXTRA_OPEN_TRACKING, true)
                putExtra(EXTRA_ORDER_ID, orderId)
            }
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(notificationId, notification)
        return Result.success()
    }

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Order Updates",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for order delivery progress"
        }
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun updateOrderStatusToCompleted(orderId: Int) {
        runCatching {
            runBlocking {
                orderApiService.updateOrderStatus(
                    UpdateOrderStatusRequest(
                        orderId = orderId,
                        orderStatus = ORDER_STATUS_COMPLETED
                    )
                )
            }
        }
    }

    private val orderApiService: ApiService by lazy {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = AppPreferences.getToken().orEmpty()
                val request = chain.request().newBuilder().apply {
                    if (token.isNotBlank()) {
                        addHeader("Authorization", "Bearer $token")
                    }
                }.build()
                chain.proceed(request)
            }
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    companion object {
        const val CHANNEL_ID = "order_progress_channel"
        const val KEY_TITLE = "key_title"
        const val KEY_MESSAGE = "key_message"
        const val KEY_NOTIFICATION_ID = "key_notification_id"
        const val KEY_ORDER_ID = "key_order_id"
        const val KEY_OPEN_TRACKING = "key_open_tracking"
        const val KEY_MARK_ORDER_COMPLETED = "key_mark_order_completed"
        const val EXTRA_OPEN_TRACKING = "extra_open_tracking"
        const val EXTRA_ORDER_ID = "extra_order_id"
        private const val ORDER_STATUS_COMPLETED = "COMPLETED"
    }
}
