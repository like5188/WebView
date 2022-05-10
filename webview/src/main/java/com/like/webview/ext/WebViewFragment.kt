package com.like.webview.ext

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.like.webview.core.X5Listener
import com.like.webview.core.addCookies
import com.like.webview.core.addJavascriptInterfaces
import com.like.webview.core.addLocalStorages
import com.tencent.smtt.export.external.interfaces.*
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import java.util.concurrent.atomic.AtomicBoolean

/*
生命周期：
// 打开包含Fragment的Activity
V/FragmentContainer: onCreate
V/FragmentContainer: onStart
V/FragmentContainer: onResume
W/Fragment1: onAttach
W/Fragment1: onCreate
W/Fragment1: onCreateView
W/Fragment1: onViewCreated
W/Fragment1: onActivityCreated
W/Fragment1: onStart
E/Fragment1: onResume
I/Fragment1: onLazyLoadData
// 转到后台运行
I/Fragment1: onPause
V/FragmentContainer: onPause
I/Fragment1: onStop
V/FragmentContainer: onStop
V/FragmentContainer: onSaveInstanceState
// 转到前台运行
V/FragmentContainer: onRestart
W/Fragment1: onStart
V/FragmentContainer: onStart
V/FragmentContainer: onResume
E/Fragment1: onResume
// 关闭Activity
I/Fragment1: onPause
V/FragmentContainer: onPause
I/Fragment1: onStop
V/FragmentContainer: onStop
I/Fragment1: onDestroyView
I/Fragment1: onDestroy
I/Fragment1: onDetach
V/FragmentContainer: onDestroy
// 旋转屏幕
I/Fragment1: onPause
V/FragmentContainer: onPause
I/Fragment1: onStop
V/FragmentContainer: onStop
V/FragmentContainer: onSaveInstanceState
I/Fragment1: onDestroyView
I/Fragment1: onDestroy
I/Fragment1: onDetach
V/FragmentContainer: onDestroy
W/Fragment1: onAttach
W/Fragment1: onCreate
V/FragmentContainer: onCreate
W/Fragment1: onCreateView
W/Fragment1: onViewCreated
W/Fragment1: onActivityCreated
W/Fragment1: onStart
V/FragmentContainer: onStart
V/FragmentContainer: onRestoreInstanceState
V/FragmentContainer: onResume
E/Fragment1: onResume
I/Fragment1: onLazyLoadData
 */

/**
 * 使用 [X5WebViewWithProgressBar] 进行了封装。
 * url 只懒加载一次。
 */
class WebViewFragment(private val getWebViewFragmentConfig: (WebViewFragment, WebView) -> WebViewFragmentConfig) : Fragment() {
    private val loaded = AtomicBoolean(false)// 懒加载控制
    private var url: String? = null
    private var x5WebViewWithProgressBar: X5WebViewWithProgressBar? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 此处如果使用 applicationContext，会造成 index.html 中的 alert 弹不出来。
        x5WebViewWithProgressBar = X5WebViewWithProgressBar(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return x5WebViewWithProgressBar?.apply {
            getWebViewFragmentConfig(this@WebViewFragment, this).let {
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

                    override fun onReceivedSslError(webView: WebView?, handler: SslErrorHandler?, error: SslError?): Boolean? {
                        return it.x5Listener?.onReceivedSslError(webView, handler, error)
                    }
                }

                addJavascriptInterfaces(it.javascriptInterfaceMap)

                // 必须要在WebView的settings设置完之后调用，即必须在 x5WebViewWithErrorViewAndProgressBar 创建完成之后调用，否则无效。
                addCookies(it.cookieMap)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        x5WebViewWithProgressBar?.onPause()
    }

    override fun onResume() {
        super.onResume()
        x5WebViewWithProgressBar?.onResume()
        if (loaded.compareAndSet(false, true)) {
            url?.let {
                x5WebViewWithProgressBar?.loadUrl(it)
            }
        }
    }

    override fun onDestroyView() {
        url = null
        loaded.compareAndSet(true, false)
        // 避免造成Fragment内存泄漏：http://42.193.188.64/articles/2021/08/09/1628511669976.html
        x5WebViewWithProgressBar?.destroy()
        x5WebViewWithProgressBar = null
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
