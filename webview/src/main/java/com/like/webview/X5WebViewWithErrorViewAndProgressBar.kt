package com.like.webview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import com.tencent.smtt.sdk.WebView

/**
 * 包含了[X5WebViewWithErrorView]，进度条[progressBar]
 */
class X5WebViewWithErrorViewAndProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {
    private var progressBar: ProgressBar? = null
    private var x5WebViewWithErrorView: X5WebViewWithErrorView? = null
    var x5Listener: X5Listener? = null

    init {
        orientation = VERTICAL
        x5WebViewWithErrorView = X5WebViewWithErrorView(context).also {
            it.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            it.x5Listener = object : X5Listener {
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

                override fun onReceivedError(webView: WebView?) {
                    x5Listener?.onReceivedError(webView)
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
            }
            addView(it)
        }
    }

    /**
     * 为X5WebView添加错误页面。
     */
    fun setErrorViewResId(@LayoutRes resId: Int) {
        x5WebViewWithErrorView?.errorView = View.inflate(context, resId, null)
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
        progressBar?.let {
            removeView(it)
        }
        progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
            // 设置进度条背景颜色
            setBackgroundColor(ContextCompat.getColor(context, progressBarBgColorResId))
            // 设置进度条颜色。设置一个ClipDrawable,ClipDrawable是对Drawable进行剪切操作，可以控制这个Drawable的剪切区域，以及相对容器的对齐方式，android中的进度条就是使用一个ClipDrawable实现效果的，它根据level的属性值，决定剪切区域的大小。
            progressDrawable = ClipDrawable(
                ColorDrawable(ContextCompat.getColor(context, progressBarProgressColorResId)),
                Gravity.START,
                ClipDrawable.HORIZONTAL
            )
            // 设置进度条高度
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, progressBarHeight.toInt())
            addView(this, 0)
        }
    }

    fun getX5WebView(): WebView? = x5WebViewWithErrorView?.tencentWebView

    /**
     * android 调用 js 方法
     *
     * @param methodName        js 方法的名字
     * @param paramsJsonString  js 方法的参数
     * @param callback          回调方法，用于处理 js 方法返回的 String 类型的结果。
     */
    fun callJsMethod(methodName: String, paramsJsonString: String? = null, callback: ((String?) -> Unit)? = null) {
        x5WebViewWithErrorView?.callJsMethod(methodName, paramsJsonString, callback)
    }

    /**
     * 需要放在super.onDestroy();之前调用，防止内存泄漏。
     */
    fun destroy() {
        progressBar = null
        x5Listener = null
        x5WebViewWithErrorView?.destroy()
        x5WebViewWithErrorView = null
    }
}
