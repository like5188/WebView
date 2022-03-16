package com.like.webview.core

import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.export.external.interfaces.WebResourceError
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
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

    override fun onReceivedError(webView: WebView?, webResourceRequest: WebResourceRequest, webResourceError: WebResourceError) {
    }

    override fun onShowCustomView(view: View?, callback: IX5WebChromeClient.CustomViewCallback?) {
    }

    override fun onHideCustomView() {
    }
}