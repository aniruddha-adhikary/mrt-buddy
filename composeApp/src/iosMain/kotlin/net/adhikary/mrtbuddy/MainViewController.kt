package net.adhikary.mrtbuddy

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import net.adhikary.mrtbuddy.database.getDatabase

fun MainViewController() = ComposeUIViewController {
    val dao = remember {
        getDatabase().getDao()
    }
    if (isDebug) {
        Napier.base(DebugAntilog())
    }
    

    App(dao = dao)

}