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
import io.github.aakira.napier.Napier

class MainActivity : ComponentActivity() {
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            ),
//            navigationBarStyle = SystemBarStyle.light(
//                scrim = Color.TRANSPARENT,
//                darkScrim = Color.TRANSPARENT
//            )
        )

        // TODO will be removed once code structure and dependancy injection is intruduced

        Napier.d("App Running.....")

        setContent {
            App(dynamicColor = true)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(dynamicColor = true)
}
