//package net.adhikary.mrtbuddy
//
//import androidx.compose.ui.window.ComposeUIViewController
//import io.github.aakira.napier.DebugAntilog
//import io.github.aakira.napier.Napier
//import kotlinx.cinterop.ExperimentalForeignApi
//import net.adhikary.mrtbuddy.di.appModule
//import net.adhikary.mrtbuddy.di.platformModule
//import org.koin.core.context.startKoin
//import org.koin.core.error.KoinApplicationAlreadyStartedException
//
//import platform.UIKit.UIApplication
//import platform.UIKit.UIColor
//import platform.UIKit.UIScreen
//import platform.UIKit.UIStatusBarStyleDarkContent
//import platform.UIKit.UIStatusBarStyleLightContent
//import platform.UIKit.UIView
//import platform.UIKit.setStatusBarStyle
//
//fun MainViewController() = ComposeUIViewController {
//    try {
//        // Initialize Koin
//        startKoin {
//            modules(appModule, platformModule)
//        }
//    } catch (e: KoinApplicationAlreadyStartedException) {
//        // Koin already started, ignore
//    }
//
//    if (isDebug) {
//        Napier.base(DebugAntilog())
//    }
//
//
//    App(
//        dynamicColor = false,
//        statusBarStyle = { mode ->
//            setStatusBarStyle(mode)
//        }
//    )
//}
//
//fun setStatusBarStyle(isDarkMode: Boolean) {
//    val statusBarStyle = if (isDarkMode) {
//        UIStatusBarStyleDarkContent
//    } else {
//        UIStatusBarStyleLightContent
//    }
//
//    UIApplication.sharedApplication.setStatusBarStyle(statusBarStyle)
//
//}
//


package net.adhikary.mrtbuddy

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.window.ComposeUIViewController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import net.adhikary.mrtbuddy.di.appModule
import net.adhikary.mrtbuddy.di.platformModule
import net.adhikary.mrtbuddy.ui.theme.toUIColor
import org.koin.core.context.startKoin
import org.koin.core.error.KoinApplicationAlreadyStartedException
import platform.CoreGraphics.CGRectMake
import platform.UIKit.*

fun MainViewController() = ComposeUIViewController {
    try {
        // Initialize Koin
        startKoin {
            modules(appModule, platformModule)
        }
    } catch (e: KoinApplicationAlreadyStartedException) {
        // Koin already started, ignore
    }

    if (isDebug) {
        Napier.base(DebugAntilog())
    }

    App(
        dynamicColor = false,
        statusBarStyle = { mode ->
            configureStatusBarAppearance(
                isDarkMode = mode,
                backgroundColor = MaterialTheme.colorScheme.background.toUIColor()
            )
        }
    )
}

@OptIn(ExperimentalForeignApi::class)
fun configureStatusBarAppearance(isDarkMode: Boolean, backgroundColor: UIColor) {
    val keyWindow = UIApplication.sharedApplication.keyWindow
    keyWindow?.let { window ->
        // Set the status bar text color
        val statusBarStyle = if (isDarkMode) {
            UIStatusBarStyleDarkContent
        } else {
            UIStatusBarStyleLightContent
        }
        UIApplication.sharedApplication.setStatusBarStyle(statusBarStyle)

        // Remove any previous status bar background views by tag



        // Create a view for the status bar background
        val screenWidth = UIScreen.mainScreen.bounds.useContents { size.width }
        val statusBarHeight = window.safeAreaInsets.useContents { top }
        val statusBarFrame = CGRectMake(0.0, 0.0, screenWidth, statusBarHeight)
        val statusBarView = UIView(frame = statusBarFrame)
        statusBarView.backgroundColor = backgroundColor
        statusBarView.tag = 12345L // Ensure the tag is a Long type in Kotlin/Native

        // Add the new status bar background view
        window.addSubview(statusBarView)
    }
}
