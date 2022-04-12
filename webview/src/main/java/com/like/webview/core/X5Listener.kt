package com.like.webview.core

import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import com.tencent.smtt.export.external.interfaces.*
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient

import com.tencent.smtt.sdk.WebView

interface X5Listener {
    fun onReceivedIcon(webView: WebView?, icon: Bitmap?)
    fun onReceivedTitle(webView: WebView?, title: String?)
    fun onProgressChanged(webView: WebView?, progress: Int?)
    fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?)
    fun onPageFinished(webView: WebView?, url: String?)
    fun onReceivedError(webView: WebView?, webResourceRequest: WebResourceRequest, webResourceError: WebResourceError)
    fun onShowFileChooser(webView: WebView?, callback: ValueCallback<Array<Uri>>?, params: WebChromeClient.FileChooserParams?): Boolean
    fun onShowCustomView(view: View?, callback: IX5WebChromeClient.CustomViewCallback?)
    fun onHideCustomView()

    /**
     * @return
     * true:不调用super.onReceivedSslError，只调用handler?.proceed()，用于测试抓包。
     * false:调用super.onReceivedSslError。
     */
    fun onReceivedSslError(webView: WebView?, handler: SslErrorHandler?, error: SslError?): Boolean?
}