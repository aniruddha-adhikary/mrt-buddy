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

class WebAppInterface(
    private val context: Context,
    private val onNavigateToCardReader: () -> Unit
) {
    @JavascriptInterface
    fun onNavigate(page: String) {
        if (page == "card-reader") {
            onNavigateToCardReader()
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FareCalculatorScreen(onNavigateToCardReader: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.allowFileAccess = true
                    settings.allowContentAccess = true
                    settings.databaseEnabled = true
                    settings.javaScriptCanOpenWindowsAutomatically = true

                    addJavascriptInterface(WebAppInterface(context, onNavigateToCardReader), "Android")

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            android.util.Log.d("WebView", "Loading URL: ${request?.url}")
                            return false
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            android.util.Log.d("WebView", "Page finished loading: $url")
                            view?.evaluateJavascript(
                                """
                                console.log('WebView loaded successfully');
                                console.log('Initializing calculator page...');
                                showPage('calculator');
                                """.trimIndent(),
                                { result ->
                                    android.util.Log.d("WebView", "JavaScript evaluation result: $result")
                                }
                            )
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: android.webkit.WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)
                            android.util.Log.e("WebView", """
                                Error loading page:
                                URL: ${request?.url}
                                Error: ${error?.description}
                                Error Code: ${error?.errorCode}
                            """.trimIndent())
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onConsoleMessage(message: android.webkit.ConsoleMessage): Boolean {
                            android.util.Log.d("WebView Console", "Message: ${message.message()}\nSource: ${message.sourceId()}\nLine: ${message.lineNumber()}\nSeverity: ${message.messageLevel()}")
                            return true
                        }

                        override fun onJsAlert(view: WebView?, url: String?, message: String?, result: android.webkit.JsResult?): Boolean {
                            android.util.Log.d("WebView JS Alert", "Alert: $message\nURL: $url")
                            return super.onJsAlert(view, url, message, result)
                        }
                    }

                    loadUrl("file:///android_asset/web/index.html")
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
