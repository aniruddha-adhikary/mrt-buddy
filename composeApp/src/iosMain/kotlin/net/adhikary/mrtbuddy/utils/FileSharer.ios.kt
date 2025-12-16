package net.adhikary.mrtbuddy.utils

import io.github.aakira.napier.Napier
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.writeToURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIAlertAction
import platform.UIKit.UIAlertActionStyleDefault
import platform.UIKit.UIAlertController
import platform.UIKit.UIAlertControllerStyleAlert
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import kotlinx.cinterop.ObjCObjectVar
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class FileSharer {
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual fun share(content: String, filename: String, mimeType: String) {
        val tempDir = NSTemporaryDirectory()
        val filePath = "$tempDir$filename"
        val fileUrl = NSURL.fileURLWithPath(filePath)

        memScoped {
            val errorPtr: ObjCObjectVar<NSError?> = alloc()
            val success = (content as NSString).writeToURL(
                fileUrl,
                atomically = true,
                encoding = NSUTF8StringEncoding,
                error = errorPtr.ptr
            )
            if (!success) {
                val error = errorPtr.value
                val errorMessage = error?.localizedDescription ?: "Unknown error"
                Napier.e("Failed to write CSV file: $errorMessage")
                throw RuntimeException("Failed to write CSV file: $errorMessage")
            }
        }

        val activityViewController = UIActivityViewController(
            activityItems = listOf(fileUrl),
            applicationActivities = null
        )

        val presentingViewController = getTopViewController()
        if (presentingViewController == null) {
            Napier.e("Cannot present share sheet: no valid view controller found")
            throw RuntimeException("Failed to present share sheet: no valid view controller found")
        }

        dispatch_async(dispatch_get_main_queue()) {
            presentingViewController.presentViewController(
                activityViewController,
                animated = true,
                completion = null
            )
        }
    }

    private fun getTopViewController(): UIViewController? {
        val keyWindow = UIApplication.sharedApplication.keyWindow
            ?: UIApplication.sharedApplication.windows.firstOrNull { (it as? UIWindow)?.isKeyWindow() == true } as? UIWindow
            ?: UIApplication.sharedApplication.delegate?.window

        val rootViewController = keyWindow?.rootViewController ?: return null

        return findTopViewController(rootViewController)
    }

    private fun findTopViewController(viewController: UIViewController): UIViewController {
        val presented = viewController.presentedViewController
        if (presented != null) {
            return findTopViewController(presented)
        }
        return viewController
    }
}
