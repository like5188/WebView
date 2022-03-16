package com.like.webview.ext

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.like.webview.core.*
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.export.external.interfaces.WebResourceError
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
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
 * 包含了 [X5WebViewWithProgressBar] 的封装。
 * url 只懒加载一次。
 */
class WebViewFragment(private val getWebViewFragmentConfig: (WebViewFragment, WebView) -> WebViewFragmentConfig) : Fragment() {
    private val loaded = AtomicBoolean(false)// 懒加载控制
    private var url: String? = null
    private val x5WebViewWithProgressBar by lazy {
        X5WebViewWithProgressBar(requireContext().applicationContext)
    }
    private val x5WebView by lazy {
        x5WebViewWithProgressBar.x5WebView
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return x5WebViewWithProgressBar.apply {
            getWebViewFragmentConfig(this@WebViewFragment, x5WebView).let {
                this@WebViewFragment.url = it.url

                setProgressBar(
                    it.progressBarHeight,
                    it.progressBarBgColorResId,
                    it.progressBarProgressColorResId
                )

                x5Listener = object : X5Listener {
                    override fun onShowFileChooser(
                        webView: WebView?,
                        callback: ValueCallback<Array<Uri>>?,
                        params: WebChromeClient.FileChooserParams?
                    ): Boolean {
                        return it.x5Listener?.onShowFileChooser(webView, callback, params) ?: false
                    }

                    override fun onReceivedIcon(webView: WebView?, icon: Bitmap?) {
                        it.x5Listener?.onReceivedIcon(webView, icon)
                    }

                    override fun onReceivedTitle(webView: WebView?, title: String?) {
                        it.x5Listener?.onReceivedTitle(webView, title)
                    }

                    override fun onProgressChanged(webView: WebView?, progress: Int?) {
                        it.x5Listener?.onProgressChanged(webView, progress)
                    }

                    override fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?) {
                        it.x5Listener?.onPageStarted(webView, url, favicon)
                        // 必须要在 X5Listener 中调用，否则无效。
                        webView?.addLocalStorages(it.localStorageMap)
                    }

                    override fun onPageFinished(webView: WebView?, url: String?) {
                        it.x5Listener?.onPageFinished(webView, url)
                    }

                    override fun onReceivedError(
                        webView: WebView?,
                        webResourceRequest: WebResourceRequest,
                        webResourceError: WebResourceError
                    ) {
                        it.x5Listener?.onReceivedError(webView, webResourceRequest, webResourceError)
                    }

                    override fun onShowCustomView(view: View?, callback: IX5WebChromeClient.CustomViewCallback?) {
                        it.x5Listener?.onShowCustomView(view, callback)
                    }

                    override fun onHideCustomView() {
                        it.x5Listener?.onHideCustomView()
                    }
                }

                x5WebView.addJavascriptInterfaces(it.javascriptInterfaceMap)

                // 必须要在WebView的settings设置完之后调用，即必须在 x5WebViewWithErrorViewAndProgressBar 创建完成之后调用，否则无效。
                addCookies(it.cookieMap)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        x5WebView.onPause()
    }

    override fun onResume() {
        super.onResume()
        x5WebView.onResume()
        if (loaded.compareAndSet(false, true)) {
            url?.let {
                x5WebView.loadUrl(it)
            }
        }
    }

    override fun onDestroyView() {
        loaded.compareAndSet(true, false)
        clearCookies()
        x5WebView.clearLocalStorages()
        // 避免造成Fragment内存泄漏：http://42.193.188.64/articles/2021/08/09/1628511669976.html
        x5WebViewWithProgressBar.destroy()
        super.onDestroyView()
    }

}

class WebViewFragmentConfig {
    /**
     * [url] 的加载时机是在第一次 [Fragment.onResume] 时。如果不传此参数，可以自行调用 [WebView.loadUrl] 方法。
     */
    var url: String? = null

    /**
     * 进度条高度，设置为 0 即隐藏进度条。
     */
    var progressBarHeight: Float = 0f

    /**
     * 进度条背景色
     */
    var progressBarBgColorResId: Int = -1

    /**
     * 进度条颜色
     */
    var progressBarProgressColorResId: Int = -1

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
