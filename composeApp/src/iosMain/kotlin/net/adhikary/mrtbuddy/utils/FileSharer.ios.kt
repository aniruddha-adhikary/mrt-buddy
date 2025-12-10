package net.adhikary.mrtbuddy.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.writeToURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class FileSharer {
    @OptIn(ExperimentalForeignApi::class)
    actual fun share(content: String, filename: String, mimeType: String) {
        val tempDir = NSTemporaryDirectory()
        val filePath = "$tempDir$filename"
        val fileUrl = NSURL.fileURLWithPath(filePath)

        (content as NSString).writeToURL(
            fileUrl,
            atomically = true,
            encoding = NSUTF8StringEncoding,
            error = null
        )

        val activityViewController = UIActivityViewController(
            activityItems = listOf(fileUrl),
            applicationActivities = null
        )

        val keyWindow = UIApplication.sharedApplication.keyWindow
        keyWindow?.rootViewController?.presentViewController(
            activityViewController,
            animated = true,
            completion = null
        )
    }
}
