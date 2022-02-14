package com.like.webview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.like.webview.databinding.FragmentWebviewBinding
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 包含了进度条的 WebView 的封装
 *
 * 注意：
 * 1、WebViewFragment 中的方法必须在 initViews() 方法执行完毕之后调用才有效。
 * 因为 FragmentTransaction 的 commit() 方法是异步的，所以我们不知道什么时候 WebViewFragment 会被创建并添加到 Activity 中。
 * 所以，如果 Activity 在 onCreate() 方法中添加了 WebViewFragment，那么就需要在 onStart()或者onResume()方法中调用相关方法才有效，具体情况有所不同。
 */
class WebViewFragment(private val url: String?, private val showProgressBar: Boolean = true) : Fragment() {
    private val isLoaded = AtomicBoolean(false)
    private var mX5ProgressBarWebView: X5ProgressBarWebView? = null
    private var mWebView: WebView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentWebviewBinding>(
            inflater,
            R.layout.fragment_webview,
            container,
            false
        ) ?: throw RuntimeException("初始化 WebViewFragment 失败")
        mX5ProgressBarWebView = binding.x5ProgressBarWebView
        mWebView = mX5ProgressBarWebView?.getWebView()
        mWebView?.settings?.cacheMode = WebSettings.LOAD_NO_CACHE// 支持微信H5支付
        return binding.root
    }

    fun load(url: String?) {
        if (url.isNullOrEmpty()) {
            return
        }
        mWebView?.loadUrl(url)
    }

    fun getWebView(): WebView? {
        return mWebView
    }

    fun setListener(listener: X5Listener) {
        mX5ProgressBarWebView?.setListener(listener)
    }

    fun pageUp() {
        mWebView?.pageUp(true)
    }

    fun pageDown() {
        mWebView?.pageDown(true)
    }

    fun reload() {
        mWebView?.reload()
    }

    /**
     * js 调用 android 方法时使用
     */
    fun addJavascriptInterface(javascriptInterface: Any, interfaceName: String) {
        mWebView?.addJavascriptInterface(javascriptInterface, interfaceName)
    }

    /**
     * android 调用 js 方法
     *
     * @param methodName        js 方法的名字
     * @param paramsJsonString  js 方法的参数
     * @param callback          回调方法，用于处理 js 方法返回的 String 类型的结果。
     */
    fun callJsMethod(methodName: String, paramsJsonString: String?, callback: ((String) -> Unit)?) {
        mX5ProgressBarWebView?.callJsMethod(methodName, paramsJsonString, callback)
    }

    override fun onPause() {
        super.onPause()
        mWebView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        mWebView?.onResume()
        if (isLoaded.compareAndSet(false, true)) {
            load(url)
        }
    }

    override fun onDestroyView() {
        isLoaded.compareAndSet(true, false)
        // 避免造成Fragment内存泄漏：http://42.193.188.64/articles/2021/08/09/1628511669976.html
        mX5ProgressBarWebView?.destroy()
        mX5ProgressBarWebView = null
        mWebView = null
        super.onDestroyView()
    }

}
