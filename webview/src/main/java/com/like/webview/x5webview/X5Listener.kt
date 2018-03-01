package com.like.webview.x5webview

import android.graphics.Bitmap
import com.tencent.smtt.sdk.WebView

abstract class X5Listener {
    open fun onReceivedIcon(webView: WebView?, icon: Bitmap?) {}
    open fun onReceivedTitle(webView: WebView?, title: String?) {}
    open fun onProgressChanged(webView: WebView?, progress: Int?) {}
    open fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?) {}
    open fun onPageFinished(webView: WebView?, url: String?) {}
    open fun onReceivedError(webView: WebView?) {}
}