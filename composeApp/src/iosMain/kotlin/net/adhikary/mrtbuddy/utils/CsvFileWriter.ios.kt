package net.adhikary.mrtbuddy.utils

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileHandle
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.closeFile
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.fileHandleForWritingAtPath
import platform.Foundation.seekToEndOfFile
import platform.Foundation.writeData
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class CsvFileWriter {
    private var filePath: String? = null
    private var fileHandle: NSFileHandle? = null

    @OptIn(ExperimentalForeignApi::class)
    actual fun createFile(filename: String): String {
        val tempDir = NSTemporaryDirectory()
        val path = "$tempDir$filename"
        filePath = path

        // Create empty file
        NSFileManager.defaultManager.createFileAtPath(path, contents = null, attributes = null)

        // Open for writing
        fileHandle = NSFileHandle.fileHandleForWritingAtPath(path)

        return path
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    actual fun appendLine(line: String) {
        val handle = fileHandle ?: return
        val lineWithNewline = "$line\n"
        val data = (lineWithNewline as NSString).dataUsingEncoding(NSUTF8StringEncoding) ?: return

        handle.seekToEndOfFile()
        handle.writeData(data)
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun close(): String {
        fileHandle?.closeFile()
        fileHandle = null
        return filePath ?: ""
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun share(mimeType: String) {
        val path = filePath ?: return
        val fileUrl = NSURL.fileURLWithPath(path)

        val activityViewController = UIActivityViewController(
            activityItems = listOf(fileUrl),
            applicationActivities = null
        )

        val presentingViewController = getTopViewController() ?: return

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
