package net.adhikary.mrtbuddy.utils

import io.github.aakira.napier.Napier
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.Foundation.NSError
import platform.Foundation.NSErrorPointer
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.writeToURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import kotlinx.cinterop.ObjCObjectVar

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class FileSharer {
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual fun share(content: String, filename: String, mimeType: String) {
        val tempDir = NSTemporaryDirectory()
        val filePath = "$tempDir$filename"
        val fileUrl = NSURL.fileURLWithPath(filePath)

        val writeSuccess = memScoped {
            val errorPtr: ObjCObjectVar<NSError?> = alloc()
            val success = (content as NSString).writeToURL(
                fileUrl,
                atomically = true,
                encoding = NSUTF8StringEncoding,
                error = errorPtr.ptr
            )
            if (!success) {
                val error = errorPtr.value
                Napier.e("Failed to write CSV file: ${error?.localizedDescription ?: "Unknown error"}")
            }
            success
        }

        if (!writeSuccess) {
            return
        }

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
