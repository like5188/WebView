package com.like.webview.ui

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
import com.like.webview.*
import com.like.webview.core.X5WebViewWithErrorViewAndProgressBar
import com.like.webview.listener.X5Listener
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
 * 包含了进度条的 WebView 的封装。
 * url 只懒加载一次。分两种情况：
 * 1、先[init]，在[onResume]时会加载。
 * 2、先[onResume]，在[init]时会判断如果[isOnResume]为 true，就会直接加载，如果[isOnResume]为 false，就会等下次[onResume]时加载。
 */
open class WebViewFragment : Fragment() {
    private val isOnResume = AtomicBoolean(false)
    private val isLoaded = AtomicBoolean(false)// 懒加载控制
    private var webViewFragmentConfig: WebViewFragmentConfig? = null
    private var x5WebViewWithErrorViewAndProgressBar: X5WebViewWithErrorViewAndProgressBar? = null

    fun init(webViewFragmentConfig: WebViewFragmentConfig?) {
        webViewFragmentConfig ?: return
        if (this.webViewFragmentConfig == null) {
            this.webViewFragmentConfig = webViewFragmentConfig
            x5WebViewWithErrorViewAndProgressBar?.apply {
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
                        // 必须要在 X5Listener 中调用，否则无效。
                        webView?.addLocalStorages(webViewFragmentConfig.localStorageMap)
                    }

                    override fun onPageFinished(webView: WebView?, url: String?) {
                        webViewFragmentConfig.x5Listener?.onPageFinished(webView, url)
                    }

                    override fun onReceivedError(webView: WebView?) {
                        webViewFragmentConfig.x5Listener?.onReceivedError(webView)
                    }
                }

                x5WebViewWithErrorView?.errorView = View.inflate(context, webViewFragmentConfig.errorViewResId, null)
                getX5WebView()?.addJavascriptInterfaces(webViewFragmentConfig.javascriptInterfaceMap)

                // 必须要在WebView的settings设置完之后调用，否则无效。
                addCookies(webViewFragmentConfig.cookieMap)
            }
        }
        if (isOnResume.get() && isLoaded.compareAndSet(false, true)) {
            getX5WebView()?.loadUrl(webViewFragmentConfig.url)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return X5WebViewWithErrorViewAndProgressBar(requireContext()).apply {
            x5WebViewWithErrorViewAndProgressBar = this
        }
    }

    override fun onPause() {
        super.onPause()
        getX5WebView()?.onPause()
        isOnResume.set(false)
    }

    override fun onResume() {
        super.onResume()
        getX5WebView()?.onResume()
        isOnResume.set(true)
        init(webViewFragmentConfig)
    }

    override fun onDestroyView() {
        isLoaded.compareAndSet(true, false)
        clearCookies()
        getX5WebView()?.clearLocalStorages()
        webViewFragmentConfig = null
        // 避免造成Fragment内存泄漏：http://42.193.188.64/articles/2021/08/09/1628511669976.html
        x5WebViewWithErrorViewAndProgressBar?.destroy()
        x5WebViewWithErrorViewAndProgressBar = null
        super.onDestroyView()
    }

    fun getX5WebView(): WebView? {
        return x5WebViewWithErrorViewAndProgressBar?.x5WebViewWithErrorView?.x5WebView
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
}
