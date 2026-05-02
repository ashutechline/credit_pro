package com.education.creditscore.calculator

import com.education.creditscore.calculator.R
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CreditProApplication : Application() {

    lateinit var analytics: FirebaseAnalytics
        private set

    override fun onCreate() {
        super.onCreate()

        // Firebase
        FirebaseApp.initializeApp(this)
        analytics = FirebaseAnalytics.getInstance(this)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)


        // Notification channels
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            val scoreChannel = NotificationChannel(
                CHANNEL_SCORE,
                getString(R.string.channel_score_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = getString(R.string.channel_score_desc) }

            val tipsChannel = NotificationChannel(
                CHANNEL_TIPS,
                getString(R.string.channel_tips_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = getString(R.string.channel_tips_desc) }

            val remindersChannel = NotificationChannel(
                CHANNEL_REMINDERS,
                getString(R.string.channel_reminders_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = getString(R.string.channel_reminders_desc) }

            manager.createNotificationChannels(listOf(scoreChannel, tipsChannel, remindersChannel))
        }
    }

    companion object {
        const val CHANNEL_SCORE = "channel_score"
        const val CHANNEL_TIPS = "channel_tips"
        const val CHANNEL_REMINDERS = "channel_reminders"
    }
}
