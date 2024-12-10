package net.adhikary.mrtbuddy

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import io.github.aakira.napier.Napier

class MainActivity : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()

        // TODO will be removed once code structure and dependancy injection is intruduced

        Napier.d("App Running.....")

        setContent {

            App(
                statusBarStyle = { mode ->
                    setStatusBarStyle(this, mode)
                },
                dynamicColor = true
            )
        }
    }
}

private fun setStatusBarStyle(activity: ComponentActivity, isDarkMode: Boolean) {
    val window = activity.window
    WindowCompat.setDecorFitsSystemWindows(window, false)

    val insetsController = WindowInsetsControllerCompat(window, window.decorView)
    insetsController.isAppearanceLightStatusBars = !isDarkMode // true for light content, false for dark
}


@Preview
@Composable
fun AppAndroidPreview() {
    App(statusBarStyle = {}, dynamicColor = true)
}
