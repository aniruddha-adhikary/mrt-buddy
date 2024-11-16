package net.adhikary.mrtbuddy.ui.screens.licenses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLFile

@Composable
fun OpenSourceLicensesScreen(
    onBack: () -> Unit = {}
) {
    MaterialTheme {
        Column(Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Open Source Licenses") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
            val webViewState = rememberWebViewStateWithHTMLFile("open-source-licenses.html")
            WebView(
                state = webViewState,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
