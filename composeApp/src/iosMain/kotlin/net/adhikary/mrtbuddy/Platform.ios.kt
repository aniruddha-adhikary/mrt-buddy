package net.adhikary.mrtbuddy

import platform.UIKit.UIDevice
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform as kotlinPlatform

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

@OptIn(ExperimentalNativeApi::class)
actual val isDebug: Boolean = kotlinPlatform.isDebugBinary