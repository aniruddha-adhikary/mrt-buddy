package net.adhikary.mrtbuddy.notification

class IosNotificationManager : NotificationManager {
    override fun scheduleNotification() {
        NotificationHelper.scheduleNotification()
    }

    override fun cancelNotification() {
        NotificationHelper.cancelNotification()
    }
}