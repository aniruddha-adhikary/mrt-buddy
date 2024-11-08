package net.adhikary.mrtbuddy

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.adhikary.mrtbuddy.database.getDatabaseBuilder
import net.adhikary.mrtbuddy.database.getRoomDatabase

class MainActivity : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        demoDbCall(applicationContext) // for checking the impl
        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}

fun demoDbCall(context: Context) {
    val scope = CoroutineScope(Dispatchers.IO)

    val dbBuilder = getDatabaseBuilder(context)
    val roomDatabase = getRoomDatabase(dbBuilder)

    scope.launch {
        val list = roomDatabase.getDao().getAll()
        println("db list isze ${list.size}")
    }


}
