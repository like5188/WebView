package com.like.webview.component

import android.content.Context
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.like.webview.X5Listener
import com.like.webview.component.service.WebViewService
import com.tencent.smtt.sdk.WebView

@Route(path = Consts.SERVICE_WEB_VIEW, name = "登录页面路由服务")
class WebViewServiceImpl : WebViewService {
    private var mContext: Context? = null
    private var mWebViewFragment: WebViewFragment? = null

    override fun init(context: Context) {
        mContext = context
    }

    override fun getFragment(url: String): Fragment {
        val fragment = ARouter.getInstance().build(Consts.UI_WEB_VIEW_FRAGMENT)
                .withString("url", url)
                .navigation() as WebViewFragment
        mWebViewFragment = fragment
        return fragment
    }

    override fun getWebView(): WebView? {
        return mWebViewFragment?.getWebView()
    }

    override fun setListener(listener: X5Listener) {
        mWebViewFragment?.setListener(listener)
    }

    override fun setInterfaceName(interfaceName: String) {
        mWebViewFragment?.setInterfaceName(interfaceName)
    }

    override fun registerAndroidMethodForJSCall(methodName: String, method: (String) -> String) {
        mWebViewFragment?.registerAndroidMethodForJSCall(methodName, method)
    }

    override fun callJSMethod(methodName: String, paramsJsonString: String?, callback: ((String) -> Unit)?) {
        mWebViewFragment?.callJSMethod(methodName, paramsJsonString, callback)
    }

    override fun pageUp() {
        mWebViewFragment?.pageUp()
    }

    override fun pageDown() {
        mWebViewFragment?.pageDown()
    }

    override fun reload() {
        mWebViewFragment?.reload()
    }

}
