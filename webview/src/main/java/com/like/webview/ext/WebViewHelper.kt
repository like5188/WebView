package com.like.webview.ext

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import com.like.webview.core.X5Listener
import com.like.webview.core.addCookies
import com.like.webview.core.addJavascriptInterfaces
import com.like.webview.core.addLocalStorages
import com.tencent.smtt.export.external.interfaces.*
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 使用 [X5WebViewWithProgressBar] 进行了生命周期封装。便于 Activity 和 Fragment 使用。
 * url 只懒加载一次。
 */
class WebViewHelper {
    private val loaded = AtomicBoolean(false)// 懒加载控制
    var x5WebViewWithProgressBar: X5WebViewWithProgressBar? = null
        private set
    private var webViewConfig: WebViewConfig? = null

    fun onCreate(context: Context, getWebViewConfig: (X5WebViewWithProgressBar) -> WebViewConfig) {
        // 此处如果使用 applicationContext，会造成 index.html 中的 alert 弹不出来。
        if (x5WebViewWithProgressBar == null) {
            x5WebViewWithProgressBar = X5WebViewWithProgressBar(context).apply {
                if (webViewConfig == null) {
                    webViewConfig = getWebViewConfig(this)
                }
            }
        }
        initWebViewConfig(x5WebViewWithProgressBar, webViewConfig)
    }

    fun onPause() {
        x5WebViewWithProgressBar?.onPause()
    }

    fun onResume() {
        x5WebViewWithProgressBar?.onResume()
        if (loaded.compareAndSet(false, true)) {
            x5WebViewWithProgressBar?.loadUrl(webViewConfig?.url)
        }
    }

    fun onDestroy() {
        loaded.compareAndSet(true, false)
        x5WebViewWithProgressBar?.destroy()
        x5WebViewWithProgressBar = null
        webViewConfig = null
    }

    private fun initWebViewConfig(webView: X5WebViewWithProgressBar?, config: WebViewConfig?) {
        webView ?: return
        config ?: return
        webView.setProgressBar(
            config.progressBarHeight,
            config.progressBarBgColorResId,
            config.progressBarProgressColorResId
        )

        webView.x5Listener = object : X5Listener {
            override fun onShowFileChooser(
                webView: WebView?,
                callback: ValueCallback<Array<Uri>>?,
                params: WebChromeClient.FileChooserParams?
            ): Boolean {
                return config.x5Listener?.onShowFileChooser(webView, callback, params) ?: false
            }

            override fun onReceivedIcon(webView: WebView?, icon: Bitmap?) {
                config.x5Listener?.onReceivedIcon(webView, icon)
            }

            override fun onReceivedTitle(webView: WebView?, title: String?) {
                config.x5Listener?.onReceivedTitle(webView, title)
            }

            override fun onProgressChanged(webView: WebView?, progress: Int?) {
                config.x5Listener?.onProgressChanged(webView, progress)
            }

            override fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?) {
                config.x5Listener?.onPageStarted(webView, url, favicon)
                // 必须要在 X5Listener 中调用，否则无效。
                webView?.addLocalStorages(config.localStorageMap)
            }

            override fun onPageFinished(webView: WebView?, url: String?) {
                config.x5Listener?.onPageFinished(webView, url)
            }

            override fun onReceivedError(
                webView: WebView?,
                webResourceRequest: WebResourceRequest,
                webResourceError: WebResourceError
            ) {
                config.x5Listener?.onReceivedError(webView, webResourceRequest, webResourceError)
            }

            override fun onShowCustomView(view: View?, callback: IX5WebChromeClient.CustomViewCallback?) {
                config.x5Listener?.onShowCustomView(view, callback)
            }

            override fun onHideCustomView() {
                config.x5Listener?.onHideCustomView()
            }

            override fun onReceivedSslError(webView: WebView?, handler: SslErrorHandler?, error: SslError?): Boolean? {
                return config.x5Listener?.onReceivedSslError(webView, handler, error)
            }
        }

        webView.addJavascriptInterfaces(config.javascriptInterfaceMap)

        // 必须要在WebView的settings设置完之后调用，即必须在 x5WebViewWithErrorViewAndProgressBar 创建完成之后调用，否则无效。
        addCookies(config.cookieMap)
    }

}