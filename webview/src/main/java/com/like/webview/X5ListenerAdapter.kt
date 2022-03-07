package com.like.webview

import android.graphics.Bitmap

import com.tencent.smtt.sdk.WebView

open class X5ListenerAdapter : X5Listener {
    override fun onReceivedIcon(webView: WebView?, icon: Bitmap?) {
    }

    override fun onReceivedTitle(webView: WebView?, title: String?) {
    }

    override fun onProgressChanged(webView: WebView?, progress: Int?) {
    }

    override fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?) {
    }

    override fun onPageFinished(webView: WebView?, url: String?) {
    }

    override fun onReceivedError(webView: WebView?) {
    }
}