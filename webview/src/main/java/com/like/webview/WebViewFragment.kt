package com.like.webview

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 包含了进度条的 WebView 的封装
 */
class WebViewFragment(private val webViewFragmentConfig: WebViewFragmentConfig) : Fragment() {
    private val isLoaded = AtomicBoolean(false)// 懒加载控制
    private var x5WebViewWithErrorViewAndProgressBar: X5WebViewWithErrorViewAndProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return X5WebViewWithErrorViewAndProgressBar(requireContext()).apply {
            x5WebViewWithErrorViewAndProgressBar = this
            setProgressBar(
                webViewFragmentConfig.progressBarHeight,
                webViewFragmentConfig.progressBarBgColorResId,
                webViewFragmentConfig.progressBarProgressColorResId
            )
            x5Listener = webViewFragmentConfig.x5Listener
            x5WebViewWithErrorView?.errorView = View.inflate(context, webViewFragmentConfig.errorViewResId, null)
            x5WebViewWithErrorView?.tencentWebView?.apply {
                settings?.cacheMode = WebSettings.LOAD_NO_CACHE// 支持微信H5支付
                webViewFragmentConfig.javascriptInterfaceMap.forEach {
                    addJavascriptInterface(it.value, it.key)
                }
            }
        }
    }

    fun getX5WebViewWithErrorViewAndProgressBar(): X5WebViewWithErrorViewAndProgressBar? {
        return x5WebViewWithErrorViewAndProgressBar
    }

    fun getX5WebViewWithErrorView(): X5WebViewWithErrorView? {
        return getX5WebViewWithErrorViewAndProgressBar()?.x5WebViewWithErrorView
    }

    fun getX5WebView(): WebView? {
        return getX5WebViewWithErrorView()?.tencentWebView
    }

    fun load(url: String?) {
        if (url.isNullOrEmpty()) {
            return
        }
        getX5WebView()?.loadUrl(url)
    }

    fun pageUp() {
        getX5WebView()?.pageUp(true)
    }

    fun pageDown() {
        getX5WebView()?.pageDown(true)
    }

    fun reload() {
        getX5WebView()?.reload()
    }

    fun localStorage(key: String, value: String) {
        getX5WebViewWithErrorView()?.localStorage(key, value)
    }

    /**
     * android 调用 js 方法
     *
     * @param methodName        js 方法的名字
     * @param paramsJsonString  js 方法的参数
     * @param callback          回调方法，用于处理 js 方法返回的 String 类型的结果。
     */
    fun callJsMethod(methodName: String, paramsJsonString: String? = null, callback: ((String?) -> Unit)? = null) {
        getX5WebViewWithErrorView()?.callJsMethod(methodName, paramsJsonString, callback)
    }

    override fun onPause() {
        super.onPause()
        getX5WebView()?.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.e("Logger", "WebViewFragment onResume")
        getX5WebView()?.onResume()
        if (isLoaded.compareAndSet(false, true)) {
            load(webViewFragmentConfig.url)
        }
    }

    override fun onDestroyView() {
        Log.e("Logger", "WebViewFragment onDestroyView")
        isLoaded.compareAndSet(true, false)
        // 避免造成Fragment内存泄漏：http://42.193.188.64/articles/2021/08/09/1628511669976.html
        x5WebViewWithErrorViewAndProgressBar?.destroy()
        x5WebViewWithErrorViewAndProgressBar = null
        super.onDestroyView()
    }

}

class WebViewFragmentConfig {
    /**
     * [url] 的加载时机是在第一次 [onResume] 时。如果不传此参数，可以自行调用 [load] 方法。
     */
    var url: String? = null

    /**
     * 错误页面资源 id
     */
    @LayoutRes
    var errorViewResId: Int = R.layout.webview_error_view

    /**
     * 进度条高度，设置为 0 即隐藏进度条。
     */
    var progressBarHeight: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, Resources.getSystem().displayMetrics)

    /**
     * 进度条背景色
     */
    var progressBarBgColorResId: Int = R.color.colorPrimary

    /**
     * 进度条颜色
     */
    var progressBarProgressColorResId: Int = R.color.colorPrimaryDark

    /**
     * 注册 js 调用 android 方法
     */
    val javascriptInterfaceMap = mutableMapOf<String, Any>()

    var x5Listener: X5Listener? = null
}
