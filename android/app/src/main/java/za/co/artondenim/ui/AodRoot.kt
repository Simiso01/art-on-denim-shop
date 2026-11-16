package za.co.artondenim.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.view.View
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import za.co.artondenim.BuildConfig

object HandoffBus {
    var code by mutableStateOf<String?>(null)
}

private class UploadBridge {
    var callback: ValueCallback<Array<Uri>>? = null
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AodRoot() {
    val site = BuildConfig.API_BASE.substringBefore("/api/") + "/"
    val uploads = remember { UploadBridge() }
    var webView by remember { mutableStateOf<WebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    val chooser = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val files = WebChromeClient.FileChooserParams.parseResult(result.resultCode, result.data)
        uploads.callback?.onReceiveValue(files)
        uploads.callback = null
    }

    BackHandler(enabled = canGoBack) { webView?.goBack() }

    AndroidView(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadsImagesAutomatically = true
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                settings.setSupportZoom(false)
                settings.builtInZoomControls = false
                settings.displayZoomControls = false
                settings.javaScriptCanOpenWindowsAutomatically = true
                settings.mediaPlaybackRequiresUserGesture = true
                isHorizontalScrollBarEnabled = false
                overScrollMode = View.OVER_SCROLL_NEVER
                CookieManager.getInstance().setAcceptCookie(true)
                CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                        val target = request.url ?: return false
                        val scheme = target.scheme.orEmpty()
                        if (scheme == "http" || scheme == "https") return false
                        if (scheme == "aod") {
                            view.loadUrl(site + "pages/account.php")
                            return true
                        }
                        val activity = context.findActivity()
                        activity.startActivity(Intent(Intent.ACTION_VIEW, target))
                        return true
                    }

                    override fun onPageFinished(view: WebView, url: String?) {
                        view.evaluateJavascript(
                            """
                            (function () {
                              var meta = document.querySelector('meta[name=viewport]');
                              if (meta) {
                                meta.setAttribute('content', 'width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no');
                              }
                              document.documentElement.style.overflowX = 'hidden';
                              document.body.style.overflowX = 'hidden';
                              document.documentElement.style.maxWidth = '100%';
                              document.body.style.maxWidth = '100%';
                            })();
                            """.trimIndent(),
                            null
                        )
                    }

                    override fun doUpdateVisitedHistory(view: WebView, url: String?, isReload: Boolean) {
                        canGoBack = view.canGoBack()
                    }
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onShowFileChooser(
                        webView: WebView,
                        filePathCallback: ValueCallback<Array<Uri>>,
                        fileChooserParams: FileChooserParams
                    ): Boolean {
                        uploads.callback?.onReceiveValue(null)
                        uploads.callback = filePathCallback
                        val intent = fileChooserParams.createIntent()
                        chooser.launch(intent)
                        return true
                    }
                }
                webView = this
                loadUrl(site)
            }
        }
    )
}

private fun Context.findActivity(): Activity {
    var current = this
    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }
    error("Art on Denim has no window for this link.")
}
