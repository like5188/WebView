package com.like.webview.ext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.like.webview.core.X5Listener
import com.like.webview.core.X5WebView
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.export.external.interfaces.WebResourceError
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView

/**
 * 包含了[X5WebView]，进度条[progressBar]
 */
class X5WebViewWithProgressBar(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {
    private var progressBar: ProgressBar? = null
    var x5Listener: X5Listener? = null
    val x5WebView by lazy {
        X5WebView(context).also {
            it.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
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

                override fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?) {
                    x5Listener?.onPageStarted(webView, url, favicon)
                }

                override fun onPageFinished(webView: WebView?, url: String?) {
                    x5Listener?.onPageFinished(webView, url)
                }

                override fun onReceivedError(
                    webView: WebView?,
                    webResourceRequest: WebResourceRequest,
                    webResourceError: WebResourceError
                ) {
                    x5Listener?.onReceivedError(webView, webResourceRequest, webResourceError)
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
                    x5Listener?.onProgressChanged(webView, progress)
                }

                override fun onShowCustomView(view: View?, callback: IX5WebChromeClient.CustomViewCallback?) {
                    x5Listener?.onShowCustomView(view, callback)
                }

                override fun onHideCustomView() {
                    x5Listener?.onHideCustomView()
                }
            }
            addView(it)
        }
    }

    init {
        orientation = VERTICAL
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
                layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, progressBarHeight.toInt())
                addView(this, 0)
            }
        }
    }

    /**
     * 需要放在super.onDestroy();之前调用，防止内存泄漏。
     */
    fun destroy() {
        progressBar = null
        x5Listener = null
        x5WebView.destroy()
    }
}
