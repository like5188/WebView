package com.like.webview.core

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView

internal class X5WebChromeClient(private val mListener: X5Listener?) : WebChromeClient() {
    companion object {
        private val TAG = X5WebChromeClient::class.java.simpleName
    }

    override fun onShowFileChooser(p0: WebView?, p1: ValueCallback<Array<Uri>>?, p2: FileChooserParams?): Boolean {
        Log.d(TAG, "onShowFileChooser")
        return mListener?.onShowFileChooser(p0, p1, p2) ?: super.onShowFileChooser(p0, p1, p2)
    }

    override fun onReceivedIcon(webView: WebView?, icon: Bitmap?) {
        super.onReceivedIcon(webView, icon)
        mListener?.onReceivedIcon(webView, icon)
        Log.d(TAG, "onReceivedIcon")
    }

    override fun onReceivedTitle(webView: WebView?, title: String?) {
        super.onReceivedTitle(webView, title)
        mListener?.onReceivedTitle(webView, title)
        Log.d(TAG, "onReceivedTitle title=$title")
    }

    override fun onProgressChanged(webView: WebView?, i: Int) {
        super.onProgressChanged(webView, i)
        mListener?.onProgressChanged(webView, i)
        Log.d(TAG, "onProgressChanged i=$i")
    }

    override fun onShowCustomView(p0: View?, p1: IX5WebChromeClient.CustomViewCallback?) {
        super.onShowCustomView(p0, p1)
        mListener?.onShowCustomView(p0, p1)
        Log.d(TAG, "onShowCustomView")
    }

    override fun onHideCustomView() {
        super.onHideCustomView()
        mListener?.onHideCustomView()
        Log.d(TAG, "onHideCustomView")
    }

}
