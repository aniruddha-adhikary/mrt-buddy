package net.adhikary.mrtbuddy.notification

import android.content.Context

class AndroidNotificationManager(private val context: Context) : NotificationManager {
    override fun scheduleNotification() {
        NotificationHelper.scheduleNotification(context)
    }

    override fun cancelNotification() {
        NotificationHelper.cancelNotification(context)
    }
}