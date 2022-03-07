package com.like.webview

import android.content.Context
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
 *
 * 注意：
 * 1、如果使用 FragmentTransaction 的 commit() 方法来添加，因为此方法是异步的，所以我们不知道什么时候 WebViewFragment 会被创建并添加到 Activity 中。
 * 所以，如果 Activity 在 onCreate() 方法中添加了 WebViewFragment，那么就需要在 onStart()或者onResume()方法中调用[WebViewFragment]里面的相关方法才有效，具体情况有所不同。
 * 2、如果在 Activity 的 onCreate 方法中进行 WebView 相关的操作，不会成功，
 * 因为 [x5WebView]、[x5WebViewWithErrorViewAndProgressBar]都为 null。
 * 所以相关操作都要放到 onStart()或者onResume()（如果不需要懒加载 url）方法中。
 */
class WebViewFragment(private val webViewFragmentConfig: WebViewFragmentConfig) : Fragment() {
    private val isLoaded = AtomicBoolean(false)
    private var x5WebViewWithErrorViewAndProgressBar: X5WebViewWithErrorViewAndProgressBar? = null
    private var x5WebView: WebView? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e("Logger", "WebViewFragment onAttach")
        x5WebViewWithErrorViewAndProgressBar = X5WebViewWithErrorViewAndProgressBar(context).apply {
            x5WebView = getX5WebView()?.apply {
                settings?.cacheMode = WebSettings.LOAD_NO_CACHE// 支持微信H5支付
                webViewFragmentConfig.javascriptInterfaceMap.forEach {
                    addJavascriptInterface(it.value, it.key)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("Logger", "WebViewFragment onCreateView x5WebViewWithErrorViewAndProgressBar=$x5WebViewWithErrorViewAndProgressBar")
        return x5WebViewWithErrorViewAndProgressBar?.apply {
            setErrorViewResId(webViewFragmentConfig.errorViewResId)
            setProgressBar(
                webViewFragmentConfig.progressBarHeight,
                webViewFragmentConfig.progressBarBgColorResId,
                webViewFragmentConfig.progressBarProgressColorResId
            )
        }
    }

    fun load(url: String?) {
        if (url.isNullOrEmpty()) {
            return
        }
        x5WebView?.loadUrl(url)
    }

    fun getWebView(): WebView? {
        return x5WebView
    }

    fun setListener(listener: X5Listener) {
        x5WebViewWithErrorViewAndProgressBar?.x5Listener = listener
    }

    fun pageUp() {
        x5WebView?.pageUp(true)
    }

    fun pageDown() {
        x5WebView?.pageDown(true)
    }

    fun reload() {
        x5WebView?.reload()
    }

    /**
     * android 调用 js 方法
     *
     * @param methodName        js 方法的名字
     * @param paramsJsonString  js 方法的参数
     * @param callback          回调方法，用于处理 js 方法返回的 String 类型的结果。
     */
    fun callJsMethod(methodName: String, paramsJsonString: String? = null, callback: ((String?) -> Unit)? = null) {
        x5WebViewWithErrorViewAndProgressBar?.callJsMethod(methodName, paramsJsonString, callback)
    }

    override fun onPause() {
        super.onPause()
        x5WebView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.e("Logger", "WebViewFragment onResume")
        x5WebView?.onResume()
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
        x5WebView = null
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
}
