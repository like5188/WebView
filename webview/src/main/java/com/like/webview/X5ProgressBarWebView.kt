package com.like.webview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar

import com.tencent.smtt.sdk.WebView

/**
 * 包含了X5WebView，并在其基础上增加了进度条
 *
 * <attr name="error_view_res_id" format="reference|integer" />
 * <attr name="progress_bar_bg_color" format="reference|color" />
 * <attr name="progress_bar_progress_color" format="reference|color" />
 * <attr name="progress_bar_height" format="dimension|integer" />
 */
class X5ProgressBarWebView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    private val progressBar: ProgressBar by lazy {
        ProgressBar(context, attrs, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
        }
    }
    private var mListener: X5Listener? = null
    private val x5WebView: X5WebView by lazy {
        X5WebView(context).also {
            it.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            it.setListener(object : X5Listener {
                override fun onReceivedIcon(webView: WebView?, icon: Bitmap?) {
                    mListener?.onReceivedIcon(webView, icon)
                }

                override fun onReceivedTitle(webView: WebView?, title: String?) {
                    mListener?.onReceivedTitle(webView, title)
                }

                override fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?) {
                    mListener?.onPageStarted(webView, url, favicon)
                }

                override fun onPageFinished(webView: WebView?, url: String?) {
                    mListener?.onPageFinished(webView, url)
                }

                override fun onReceivedError(webView: WebView?) {
                    mListener?.onReceivedError(webView)
                }

                override fun onProgressChanged(webView: WebView?, progress: Int?) {
                    progressBar.progress = progress ?: 0
                    if (progress != 100) {
                        progressBar.visibility = View.VISIBLE
                    } else {
                        progressBar.visibility = View.GONE
                    }
                    mListener?.onProgressChanged(webView, progress)
                }
            })
        }
    }

    init {
        orientation = LinearLayout.VERTICAL
        // 获取自定义的属性
        val a = context.obtainStyledAttributes(attrs, R.styleable.X5ProgressBarWebView, defStyleAttr, 0)
        val errorViewResId = a.getResourceId(R.styleable.X5ProgressBarWebView_error_view_res_id, -1)
        val progressBarBgColor = a.getColor(R.styleable.X5ProgressBarWebView_progress_bar_bg_color, Color.parseColor("#3F51B5"))
        val progressBarProgressColor = a.getColor(R.styleable.X5ProgressBarWebView_progress_bar_progress_color, Color.parseColor("#FFFFFF"))
        val progressBarHeight = a.getDimension(R.styleable.X5ProgressBarWebView_progress_bar_height, 10f)
        a.recycle()
        // 为X5WebView添加错误页面
        if (errorViewResId != -1) {
            val errorView = View.inflate(context, errorViewResId, null)
            if (errorView != null) {
                x5WebView.setErrorView(errorView)
            }
        }
        // 设置进度条背景颜色
        progressBar.setBackgroundColor(progressBarBgColor)
        // 设置进度条颜色。设置一个ClipDrawable,ClipDrawable是对Drawable进行剪切操作，可以控制这个Drawable的剪切区域，以及相对容器的对齐方式，android中的进度条就是使用一个ClipDrawable实现效果的，它根据level的属性值，决定剪切区域的大小。
        progressBar.progressDrawable = ClipDrawable(ColorDrawable(progressBarProgressColor), Gravity.START, ClipDrawable.HORIZONTAL)
        // 设置进度条高度
        progressBar.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, progressBarHeight.toInt())

        // 添加进度条
        addView(progressBar)
        // 添加X5WebView
        addView(x5WebView)
    }

    fun getWebView() = x5WebView.getWebView()

    fun setListener(listener: X5Listener) {
        mListener = listener
    }

    /**
     * android 调用 js 方法
     *
     * @param methodName        js 方法的名字
     * @param paramsJsonString  js 方法的参数
     * @param callback          回调方法，用于处理 js 方法返回的 String 类型的结果。
     */
    fun callJsMethod(methodName: String, paramsJsonString: String? = null, callback: ((String) -> Unit)? = null) {
        x5WebView.callJsMethod(methodName, paramsJsonString, callback)
    }
}
