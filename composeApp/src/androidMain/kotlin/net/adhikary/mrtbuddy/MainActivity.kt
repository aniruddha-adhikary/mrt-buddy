package net.adhikary.mrtbuddy

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import io.github.aakira.napier.Napier
import net.adhikary.mrtbuddy.database.getDatabase

class MainActivity : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()

        // TODO will be removed once code structure and dependancy injection is intruduced

        val dao = getDatabase(applicationContext).getDao()
        val cardDao = getDatabase(applicationContext).getCardDao()
        val scanDao = getDatabase(applicationContext).getScanDao()
        val transactionDao = getDatabase(applicationContext).getTransactionDao()

        Napier.d("App Running.....")

        setContent {
            App(dao, cardDao, scanDao, transactionDao)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    val localContext = LocalContext.current
    val dao = getDatabase(localContext).getDao()
    val cardDao = getDatabase(localContext).getCardDao()
    val scanDao = getDatabase(localContext).getScanDao()
    val transactionDao = getDatabase(localContext).getTransactionDao()

    App(dao, cardDao, scanDao, transactionDao)
}
