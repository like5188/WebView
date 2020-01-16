package com.like.webview.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.like.common.base.BaseFragment
import com.like.webview.JavascriptInterface
import com.like.webview.X5Listener
import com.like.webview.X5ProgressBarWebView
import com.like.webview.component.databinding.FragmentWebviewBinding
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView

/**
 * 包含了进度条的 WebView 的封装
 *
 * 注意：
 * 1、WebViewFragment 中的方法必须在 initViews() 方法执行完毕之后调用才有效。
 * 因为 FragmentTransaction 的 commit() 方法是异步的，所以我们不知道什么时候 WebViewFragment 会被创建并添加到 Activity 中。
 * 所以，如果 Activity 在 onCreate() 方法中添加了 WebViewFragment，那么就需要在 onStart()或者onResume()方法中调用相关方法才有效，具体情况有所不同。
 *
 * 2、如果需要修改 WebView 相关的样式：
 *      ①修改错误页面
 *          在 layout 文件夹中自定义错误页面布局：
 *          webview_error_view.xml
 *      ②修改进度条颜色
 *          在 colors.xml 文件中定义：
 *          <color name="webview_progress_bar_bg_color">#E6E6E6</color>
 *          <color name="webview_progress_bar_progress_color">#FF4081</color>
 *      ③修改进度条高度
 *          在 values 文件夹中新增 dimen.xml 文件，然后在其中定义：
 *          <?xml version="1.0" encoding="utf-8"?>
 *          <resources>
 *          <dimen name="webview_progress_bar_height">3dp</dimen>
 *          </resources>
 */
@Route(path = Consts.UI_WEB_VIEW_FRAGMENT)
class WebViewFragment : BaseFragment() {
    @Autowired
    @JvmField
    var url: String? = null
    private val mJavascriptInterface: JavascriptInterface by lazy { JavascriptInterface() }
    private var mX5ProgressBarWebView: X5ProgressBarWebView? = null
    private var mWebView: WebView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentWebviewBinding>(inflater, R.layout.fragment_webview, container, false)
                ?: throw RuntimeException("初始化 WebViewFragment 失败")
        ARouter.getInstance().inject(this)
        mX5ProgressBarWebView = binding.webView
        mWebView = mX5ProgressBarWebView?.getWebView()
        mWebView?.settings?.cacheMode = WebSettings.LOAD_NO_CACHE// 支持微信H5支付
        return binding.root
    }

    override fun onLazyLoadData() {
        mWebView?.loadUrl(url)
    }

    fun getWebView(): WebView? {
        return mWebView
    }

    fun setListener(listener: X5Listener) {
        mX5ProgressBarWebView?.setListener(listener)
    }

    fun setInterfaceName(interfaceName: String) {
        mWebView?.addJavascriptInterface(mJavascriptInterface, interfaceName)
    }

    fun registerAndroidMethodForJSCall(methodName: String, method: (String) -> String) {
        mJavascriptInterface.registerAndroidMethodForJSCall(methodName, method)
    }

    fun callJSMethod(methodName: String, paramsJsonString: String? = null, callback: ((String) -> Unit)? = null) {
        val webView = mWebView ?: return
        mJavascriptInterface.callJsMethod(webView, methodName, paramsJsonString, callback)
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

    override fun onDestroy() {
        super.onDestroy()
        mWebView?.destroy()
        mJavascriptInterface.clearAndroidMethodForJSCall()
    }

}
