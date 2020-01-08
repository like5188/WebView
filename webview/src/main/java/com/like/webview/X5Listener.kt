package com.like.webview

import android.graphics.Bitmap

import com.tencent.smtt.sdk.WebView

interface X5Listener {
    fun onReceivedIcon(webView: WebView?, icon: Bitmap?)
    fun onReceivedTitle(webView: WebView?, title: String?)
    fun onProgressChanged(webView: WebView?, progress: Int?)
    fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?)
    fun onPageFinished(webView: WebView?, url: String?)
    fun onReceivedError(webView: WebView?)
}