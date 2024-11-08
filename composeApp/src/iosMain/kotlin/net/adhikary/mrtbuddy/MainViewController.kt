package net.adhikary.mrtbuddy

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import net.adhikary.mrtbuddy.database.getDatabase

fun MainViewController() = ComposeUIViewController {
    val dao = remember {
        getDatabase().getDao()
    }
    App(dao = dao)

}