package com.like.webview

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.tencent.smtt.sdk.WebView

/**
 * 包含了[X5WebViewWithErrorView]，进度条[progressBar]
 */
class X5WebViewWithErrorViewAndProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {
    private var progressBar: ProgressBar? = null
    private var mListener: X5Listener? = null
    private var x5WebViewWithErrorView: X5WebViewWithErrorView? = null

    init {
        orientation = VERTICAL
        x5WebViewWithErrorView = X5WebViewWithErrorView(context).also {
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
                    progressBar?.let { pb ->
                        pb.progress = progress ?: 0
                        if (progress != 100) {
                            pb.visibility = View.VISIBLE
                        } else {
                            pb.visibility = View.GONE
                        }
                    }
                    mListener?.onProgressChanged(webView, progress)
                }
            })
            addView(it)
        }
    }

    /**
     * 初始化，设置错误视图和进度条。
     *
     * @param errorViewResId                错误视图。如果为 -1，表示无错误视图。
     * @param progressBarHeight             进度条高度，dp。如果小于等于0，表示无进度条。
     * @param progressBarBgColorResId       进度条背景色
     * @param progressBarProgressColorResId 进度条颜色
     */
    fun init(
        errorViewResId: Int = R.layout.webview_error_view,
        progressBarHeight: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, Resources.getSystem().displayMetrics),
        progressBarBgColorResId: Int = R.color.colorPrimary,
        progressBarProgressColorResId: Int = R.color.colorPrimaryDark
    ) {
        // 为X5WebView添加错误页面
        if (errorViewResId != -1) {
            val errorView = View.inflate(context, errorViewResId, null)
            if (errorView != null) {
                x5WebViewWithErrorView?.setErrorView(errorView)
            }
        }
        if (progressBarHeight > 0 && progressBar == null) {
            progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal).apply {
                max = 100
            }.apply {
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
                // 添加进度条
                addView(this, 0)
            }
        }
    }

    fun getWebView(): WebView? = x5WebViewWithErrorView?.getWebView()

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
        x5WebViewWithErrorView?.callJsMethod(methodName, paramsJsonString, callback)
    }

    /**
     * 需要放在super.onDestroy();之前调用，防止内存泄漏。
     */
    fun destroy() {
        progressBar = null
        mListener = null
        x5WebViewWithErrorView?.destroy()
        x5WebViewWithErrorView = null
    }
}
