package com.like.webview.listener

import android.graphics.Bitmap
import android.net.Uri
import com.like.webview.listener.X5Listener
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient

import com.tencent.smtt.sdk.WebView

open class X5ListenerAdapter : X5Listener {
    override fun onShowFileChooser(
        webView: WebView?,
        callback: ValueCallback<Array<Uri>>?,
        params: WebChromeClient.FileChooserParams?
    ): Boolean {
        return false
    }

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