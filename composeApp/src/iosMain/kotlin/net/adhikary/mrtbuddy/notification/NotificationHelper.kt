package net.adhikary.mrtbuddy.notification

import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

internal object NotificationHelper {

    private const val NOTIFICATION_IDENTIFIER = "card_sync_reminder_channel"
    private const val NOTIFICATION_TITLE = "Card Sync Reminder"
    private const val NOTIFICATION_MESSAGE = "Don't lose your transaction history! Tap your card now to sync with the app."

    fun scheduleNotification() {
        val content = UNMutableNotificationContent().apply {
            setTitle(NOTIFICATION_TITLE)
            setBody(NOTIFICATION_MESSAGE)
            setSound(UNNotificationSound.defaultSound)
        }

        // Schedule the alarm to repeat (3 days = 259200 seconds)
        val interval = 3 * 24 * 60 * 60
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = interval.toDouble(),
            repeats = true
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = NOTIFICATION_IDENTIFIER,
            content = content,
            trigger = trigger
        )

        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(
            request = request,
            withCompletionHandler = { error ->
                if (error != null) {
                    println("Error scheduling notification: ${error.localizedDescription}")
                }
            }
        )
    }

    fun cancelNotification() {
        UNUserNotificationCenter.currentNotificationCenter().removePendingNotificationRequestsWithIdentifiers(
            identifiers = listOf(NOTIFICATION_IDENTIFIER)
        )
    }
}