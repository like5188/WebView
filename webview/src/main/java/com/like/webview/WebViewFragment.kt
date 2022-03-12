package com.like.webview

import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.tencent.smtt.sdk.CookieManager
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import java.util.concurrent.atomic.AtomicBoolean

/*
生命周期：
E/Logger: WebViewFragmentActivity onCreate
W/Logger: WebViewFragment onAttach
W/Logger: WebViewFragment onCreate
W/Logger: WebViewFragment onCreateView
W/Logger: WebViewFragment onViewCreated
W/Logger: WebViewFragment onStart
E/Logger: WebViewFragmentActivity onStart
E/Logger: WebViewFragmentActivity onResume
W/Logger: WebViewFragment onResume
W/Logger: WebViewFragment onPause
E/Logger: WebViewFragmentActivity onPause
W/Logger: WebViewFragment onStop
E/Logger: WebViewFragmentActivity onStop
W/Logger: WebViewFragment onDestroyView
W/Logger: WebViewFragment onDestroy
W/Logger: WebViewFragment onDetach
E/Logger: WebViewFragmentActivity onDestroy
 */

/**
 * 包含了进度条的 WebView 的封装
 */
open class WebViewFragment(private val webViewFragmentConfig: WebViewFragmentConfig) : Fragment() {
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
            x5Listener = object : X5Listener {
                override fun onShowFileChooser(
                    webView: WebView?,
                    callback: ValueCallback<Array<Uri>>?,
                    params: WebChromeClient.FileChooserParams?
                ): Boolean {
                    return webViewFragmentConfig.x5Listener?.onShowFileChooser(webView, callback, params) ?: false
                }

                override fun onReceivedIcon(webView: WebView?, icon: Bitmap?) {
                    webViewFragmentConfig.x5Listener?.onReceivedIcon(webView, icon)
                }

                override fun onReceivedTitle(webView: WebView?, title: String?) {
                    webViewFragmentConfig.x5Listener?.onReceivedTitle(webView, title)
                }

                override fun onProgressChanged(webView: WebView?, progress: Int?) {
                    webViewFragmentConfig.x5Listener?.onProgressChanged(webView, progress)
                }

                override fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?) {
                    webViewFragmentConfig.x5Listener?.onPageStarted(webView, url, favicon)
                    addLocalStorages(webViewFragmentConfig.localStorageMap)
                }

                override fun onPageFinished(webView: WebView?, url: String?) {
                    webViewFragmentConfig.x5Listener?.onPageFinished(webView, url)
                }

                override fun onReceivedError(webView: WebView?) {
                    webViewFragmentConfig.x5Listener?.onReceivedError(webView)
                }
            }
            x5WebViewWithErrorView?.errorView = View.inflate(context, webViewFragmentConfig.errorViewResId, null)
            addJavascriptInterfaces(webViewFragmentConfig.javascriptInterfaceMap)
            addCookies(webViewFragmentConfig.cookieMap)
        }
    }

    /**
     * 添加 JavascriptInterface
     */
    fun addJavascriptInterfaces(map: Map<String, Any>) {
        getX5WebView()?.apply {
            map.forEach {
                addJavascriptInterface(it.value, it.key)
            }
        }
    }

    fun removeJavascriptInterfaces() {
        getX5WebView()?.apply {
            webViewFragmentConfig.javascriptInterfaceMap.keys.forEach {
                removeJavascriptInterface(it)
            }
        }
    }

    /**
     * 添加 cookie
     * 注意：第一次设置必须要在WebView的settings设置完之后，并且在loadUrl之前调用，否则无效。
     * @param map   key：相同的key会追加；value：字符串数组，其中每个字符串的格式为"key=value"，相同的key会覆盖；
     */
    fun addCookies(map: Map<String, Array<String>>) {
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setCookies(map)
        }
    }

    fun getCookies(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        webViewFragmentConfig.cookieMap.keys.forEach {
            result[it] = getCookie(it)
        }
        return result
    }

    fun getCookie(key: String): String {
        return CookieManager.getInstance().getCookie(key) ?: ""
    }

    fun clearCookies() {
        CookieManager.getInstance().removeAllCookies(null)
    }

    /**
     * 添加 localStorage
     * 注意：第一次必须要在 X5Listener 中调用，否则无效。
     */
    fun addLocalStorages(map: Map<String, String>) {
        getX5WebViewWithErrorView()?.apply {
            map.forEach {
                setLocalStorageItem(it.key, it.value)
            }
        }
    }

    suspend fun getLocalStorages(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        webViewFragmentConfig.localStorageMap.keys.forEach {
            result[it] = getLocalStorageItem(it)
        }
        return result
    }

    suspend fun getLocalStorageItem(key: String): String {
        return getX5WebViewWithErrorView()?.getLocalStorageItem(key) ?: ""
    }

    fun clearLocalStorage() {
        getX5WebViewWithErrorView()?.clearLocalStorage()
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

    fun loadUrl(url: String?) {
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
        getX5WebView()?.onResume()
        if (isLoaded.compareAndSet(false, true)) {
            loadUrl(webViewFragmentConfig.url)
        }
    }

    override fun onDestroyView() {
        isLoaded.compareAndSet(true, false)
        removeJavascriptInterfaces()
        clearCookies()
        clearLocalStorage()
        webViewFragmentConfig.destroy()
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
    var errorViewResId: Int = R.layout.view_error_view

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

    var x5Listener: X5Listener? = null

    /**
     * js 调用 android 的所有方法
     */
    val javascriptInterfaceMap = mutableMapOf<String, Any>()

    /**
     * cookie 数据
     */
    val cookieMap = mutableMapOf<String, Array<String>>()

    /**
     * localStorage 数据
     */
    val localStorageMap = mutableMapOf<String, String>()

    fun destroy() {
        url = null
        errorViewResId = -1
        progressBarHeight = 0f
        progressBarBgColorResId = -1
        progressBarProgressColorResId = -1
        x5Listener = null
        javascriptInterfaceMap.clear()
        cookieMap.clear()
        localStorageMap.clear()
    }
}
