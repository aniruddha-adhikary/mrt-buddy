package net.adhikary.mrtbuddy

import androidx.compose.ui.window.ComposeUIViewController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import net.adhikary.mrtbuddy.di.appModule
import net.adhikary.mrtbuddy.di.platformModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {
    // Initialize Koin
    startKoin {
        modules(appModule, platformModule)
    }
    if (isDebug) {
        Napier.base(DebugAntilog())
    }

    App()
}
