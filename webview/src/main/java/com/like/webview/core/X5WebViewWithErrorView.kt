package com.like.webview.core

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.like.webview.listener.X5Listener
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView

/**
 * 包含了[X5WebView]、错误视图[errorView]
 */
class X5WebViewWithErrorView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    private var isErrorPage = false
    var x5WebView: X5WebView? = null
        private set
    var x5Listener: X5Listener? = null
    var errorView: View? = null
        set(value) {
            field?.let {
                removeView(it)
            }
            field = value?.apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                visibility = View.GONE
                addView(this, 0)
            }
        }

    init {
        x5WebView = X5WebView(context).also {
            it.layoutParams = LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            it.x5Listener = object : X5Listener {
                override fun onShowFileChooser(
                    webView: WebView?,
                    callback: ValueCallback<Array<Uri>>?,
                    params: WebChromeClient.FileChooserParams?
                ): Boolean {
                    return x5Listener?.onShowFileChooser(webView, callback, params) ?: false
                }

                override fun onReceivedIcon(webView: WebView?, icon: Bitmap?) {
                    x5Listener?.onReceivedIcon(webView, icon)
                }

                override fun onReceivedTitle(webView: WebView?, title: String?) {
                    x5Listener?.onReceivedTitle(webView, title)
                }

                override fun onProgressChanged(webView: WebView?, progress: Int?) {
                    if (!isNetworkAvailable(getContext())) {
                        showErrorView()
                    }
                    x5Listener?.onProgressChanged(webView, progress)
                }

                override fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?) {
                    isErrorPage = false
                    x5Listener?.onPageStarted(webView, url, favicon)
                }

                override fun onPageFinished(webView: WebView?, url: String?) {
                    if (!isErrorPage) {
                        showWebView()
                    }
                    x5Listener?.onPageFinished(webView, url)
                }

                override fun onReceivedError(webView: WebView?) {
                    showErrorView()
                    x5Listener?.onReceivedError(webView)
                }
            }
            addView(it)
        }
    }

    private fun showWebView() {
        errorView?.let {
            if (it.visibility != View.GONE) {
                it.visibility = View.GONE
            }
        }
        if (x5WebView?.visibility != View.VISIBLE) {
            x5WebView?.visibility = View.VISIBLE
        }
    }

    private fun showErrorView() {
        if (!isErrorViewShow()) {
            x5WebView?.clearHistory()
            errorView?.let {
                if (it.visibility != View.VISIBLE) {
                    it.visibility = View.VISIBLE
                    it.setOnClickListener { v ->
                        isErrorPage = false
                        x5WebView?.reload()
                    }
                }
            }
            if (x5WebView?.visibility != View.GONE) {
                x5WebView?.visibility = View.GONE
            }
        }
        x5WebView?.stopLoading()
        isErrorPage = true
    }

    private fun isErrorViewShow() = errorView != null && errorView!!.visibility == View.VISIBLE

    /**
     * 获取网络类型
     */
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivity.activeNetworkInfo
        return info != null && info.isConnected
    }

    /**
     * 需要放在super.onDestroy();之前调用，防止内存泄漏。
     */
    fun destroy() {
        errorView = null
        x5Listener = null
        x5WebView?.destroy()
        x5WebView = null
    }
}
