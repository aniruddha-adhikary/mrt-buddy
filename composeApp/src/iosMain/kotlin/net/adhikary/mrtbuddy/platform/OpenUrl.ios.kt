package net.adhikary.mrtbuddy.platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openUrl(url: String) {
    val nsUrl = NSURL(string = url)
    if (nsUrl != null) {
        UIApplication.sharedApplication.openURL(nsUrl, options = emptyMap<Any?, Any>()) { success ->
            if (!success) {
                println("Failed to open URL: $url")
            }
        }
    } else {
        println("Invalid URL: $url")
    }
}
