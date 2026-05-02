package com.education.creditscore.calculator.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.education.creditscore.calculator.CreditProApplication
import com.education.creditscore.calculator.R
import com.education.creditscore.calculator.activities.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.concurrent.TimeUnit

// ── Firebase Messaging Service ────────────────────────────────────────────────
class CreditProMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: getString(R.string.app_name)
        val body = remoteMessage.notification?.body ?: ""
        showNotification(title, body, CreditProApplication.CHANNEL_TIPS)
    }

    override fun onNewToken(token: String) {
        // Send token to your server if needed
    }

    private fun showNotification(title: String, body: String, channelId: String) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}

// ── Notification Receiver ─────────────────────────────────────────────────────
class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleNotifications(context)
        }
    }

    companion object {
        fun scheduleNotifications(context: Context) {
            val workManager = WorkManager.getInstance(context)

            // Daily tip notification
            val dailyTip = PeriodicWorkRequestBuilder<TipNotificationWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(8, TimeUnit.HOURS)
                .build()

            workManager.enqueueUniquePeriodicWork(
                "daily_tip",
                ExistingPeriodicWorkPolicy.KEEP,
                dailyTip
            )
        }
    }
}

// ── Tip Notification Worker ───────────────────────────────────────────────────
class TipNotificationWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val tips = DataService.tips
        val tip = tips.random()

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CreditProApplication.CHANNEL_TIPS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.money_tip_of_the_day))
            .setContentText(tip.title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(tip.body))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(1001, notification)

        return Result.success()
    }
}
