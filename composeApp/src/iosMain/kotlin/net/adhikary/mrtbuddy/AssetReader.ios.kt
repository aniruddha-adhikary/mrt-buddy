package net.adhikary.mrtbuddy

import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.stringWithContentsOfFile

actual class AssetReader {
    actual fun loadAssetContent(fileName: String): String {
        val path = NSBundle.mainBundle.pathForResource(name = fileName, ofType = null)
        return NSString.stringWithContentsOfFile(path, encoding = NSUTF8StringEncoding, error = null) ?: ""
    }
}
