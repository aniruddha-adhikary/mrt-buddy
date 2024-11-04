package net.adhikary.mrtbuddy.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.JavascriptInterface
import android.content.Context

class WebAppInterface(private val context: Context) {
    @JavascriptInterface
    fun onNavigate(page: String) {
        // Navigation callback will be implemented later
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FareCalculatorScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    addJavascriptInterface(WebAppInterface(context), "Android")
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            return false
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            // Initialize any JavaScript after page load if needed
                        }
                    }
                    webChromeClient = WebChromeClient()
                    loadUrl("file:///android_asset/web/index.html")
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
