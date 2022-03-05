package com.like.webview

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
 * 2、如果使用 FragmentTransaction 的 commitNow() 方法来添加，就不会有问题。
 */
class WebViewFragment : Fragment() {
    private val isLoaded = AtomicBoolean(false)
    private var mX5WebViewWithErrorViewAndProgressBar: X5WebViewWithErrorViewAndProgressBar? = null
    private var mWebView: WebView? = null
    private var url: String? = null
    private var errorViewResId: Int = -1
    private var progressBarHeight: Float = 0f
    private var progressBarBgColorResId: Int = -1
    private var progressBarProgressColorResId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return X5WebViewWithErrorViewAndProgressBar(requireContext()).apply {
            mX5WebViewWithErrorViewAndProgressBar = this
            mWebView = getWebView()?.apply {
                settings?.cacheMode = WebSettings.LOAD_NO_CACHE// 支持微信H5支付
            }
            init(
                errorViewResId,
                progressBarHeight,
                progressBarBgColorResId,
                progressBarProgressColorResId
            )
        }
    }

    /**
     * 初始化，设置错误视图和进度条。
     *
     * @param url   注意：[url] 的加载时机是在第一次 [onResume] 时。如果不传此参数，可以自行调用 [load] 方法。
     * @param errorViewResId                错误视图。如果为 -1，表示无错误视图。
     * @param progressBarHeight             进度条高度，dp。如果小于等于0，表示无进度条。
     * @param progressBarBgColorResId       进度条背景色
     * @param progressBarProgressColorResId 进度条颜色
     */
    fun init(
        url: String? = null,
        errorViewResId: Int = R.layout.webview_error_view,
        progressBarHeight: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, Resources.getSystem().displayMetrics),
        progressBarBgColorResId: Int = R.color.colorPrimary,
        progressBarProgressColorResId: Int = R.color.colorPrimaryDark
    ) {
        this.url = url
        this.errorViewResId = errorViewResId
        this.progressBarHeight = progressBarHeight
        this.progressBarBgColorResId = progressBarBgColorResId
        this.progressBarProgressColorResId = progressBarProgressColorResId
        // 这里也初始化一遍，避免使用者调用 init 方法的时机不对造成没有初始化。
        mX5WebViewWithErrorViewAndProgressBar?.init(
            errorViewResId,
            progressBarHeight,
            progressBarBgColorResId,
            progressBarProgressColorResId
        )
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
        mX5WebViewWithErrorViewAndProgressBar?.setListener(listener)
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
    fun callJsMethod(methodName: String, paramsJsonString: String? = null, callback: ((String?) -> Unit)? = null) {
        mX5WebViewWithErrorViewAndProgressBar?.callJsMethod(methodName, paramsJsonString, callback)
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
        mX5WebViewWithErrorViewAndProgressBar?.destroy()
        mX5WebViewWithErrorViewAndProgressBar = null
        mWebView = null
        super.onDestroyView()
    }

}
