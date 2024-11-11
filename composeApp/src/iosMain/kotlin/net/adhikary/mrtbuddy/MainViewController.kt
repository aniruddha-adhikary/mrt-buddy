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
    val cardDao = remember {
        getDatabase().getCardDao()
    }
    val scanDao = remember {
        getDatabase().getScanDao()
    }
    val transactionDao = remember {
        getDatabase().getTransactionDao()
    }
    if (isDebug) {
        Napier.base(DebugAntilog())
    }
    

    App(dao, cardDao, scanDao, transactionDao)

}