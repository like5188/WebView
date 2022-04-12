package com.like.webview.ext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.like.webview.core.X5Listener
import com.like.webview.core.X5WebView
import com.tencent.smtt.export.external.interfaces.*
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView

/**
 * 包含顶部进度条[ProgressBar]的[X5WebView]
 */
class X5WebViewWithProgressBar(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    X5WebView(context, attrs, defStyleAttr) {
    private var progressBar: ProgressBar? = null
    override var x5Listener: X5Listener? = null
        set(value) {
            field = object : X5Listener {
                override fun onShowFileChooser(
                    webView: WebView?,
                    callback: ValueCallback<Array<Uri>>?,
                    params: WebChromeClient.FileChooserParams?
                ): Boolean {
                    return value?.onShowFileChooser(webView, callback, params) ?: false
                }

                override fun onReceivedIcon(webView: WebView?, icon: Bitmap?) {
                    value?.onReceivedIcon(webView, icon)
                }

                override fun onReceivedTitle(webView: WebView?, title: String?) {
                    value?.onReceivedTitle(webView, title)
                }

                override fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?) {
                    value?.onPageStarted(webView, url, favicon)
                }

                override fun onPageFinished(webView: WebView?, url: String?) {
                    value?.onPageFinished(webView, url)
                }

                override fun onReceivedError(
                    webView: WebView?,
                    webResourceRequest: WebResourceRequest,
                    webResourceError: WebResourceError
                ) {
                    value?.onReceivedError(webView, webResourceRequest, webResourceError)
                }

                override fun onProgressChanged(webView: WebView?, progress: Int?) {
                    progressBar?.let { pb ->
                        pb.progress = progress ?: 0
                        if (progress != 100) {
                            pb.visibility = View.VISIBLE
                        } else {
                            pb.visibility = View.GONE
                        }
                    }
                    value?.onProgressChanged(webView, progress)
                }

                override fun onShowCustomView(view: View?, callback: IX5WebChromeClient.CustomViewCallback?) {
                    value?.onShowCustomView(view, callback)
                }

                override fun onHideCustomView() {
                    value?.onHideCustomView()
                }

                override fun onReceivedSslError(webView: WebView?, handler: SslErrorHandler?, error: SslError?): Boolean? {
                    return value?.onReceivedSslError(webView, handler, error)
                }
            }
        }

    /**
     * 设置进度条。
     *
     * @param progressBarHeight             进度条高度
     * @param progressBarBgColorResId       进度条背景色
     * @param progressBarProgressColorResId 进度条颜色
     */
    fun setProgressBar(
        progressBarHeight: Float,
        progressBarBgColorResId: Int,
        progressBarProgressColorResId: Int
    ) {
        if (progressBar == null && progressBarHeight > 0f) {
            progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
                max = 100
                // 设置进度条背景颜色
                try {
                    ContextCompat.getColor(context, progressBarBgColorResId)
                } catch (e: Exception) {
                    null
                }?.let {
                    setBackgroundColor(it)
                }
                // 设置进度条颜色。设置一个ClipDrawable,ClipDrawable是对Drawable进行剪切操作，可以控制这个Drawable的剪切区域，以及相对容器的对齐方式，android中的进度条就是使用一个ClipDrawable实现效果的，它根据level的属性值，决定剪切区域的大小。
                try {
                    ContextCompat.getColor(context, progressBarProgressColorResId)
                } catch (e: Exception) {
                    null
                }?.let {
                    progressDrawable = ClipDrawable(
                        ColorDrawable(it),
                        Gravity.START,
                        ClipDrawable.HORIZONTAL
                    )
                }

                // 设置进度条高度
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, progressBarHeight.toInt())
                addView(this)
            }
        }
    }

    /**
     * 需要放在super.onDestroy();之前调用，防止内存泄漏。
     */
    override fun destroy() {
        removeView(progressBar)
        progressBar = null
        super.destroy()
    }

}
