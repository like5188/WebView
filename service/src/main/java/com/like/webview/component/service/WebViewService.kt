package com.like.webview.component.service

import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.template.IProvider
import com.like.webview.X5Listener
import com.tencent.smtt.sdk.WebView

/**
 * WebView页面服务接口
 */
interface WebViewService : IProvider {
    fun getFragment(url: String): Fragment

    fun getWebView(): WebView?

    fun setListener(listener: X5Listener)

    fun setInterfaceName(interfaceName: String)

    fun registerAndroidMethodForJSCall(methodName: String, method: (String) -> String)

    fun callJSMethod(methodName: String, paramsJsonString: String? = null, callback: ((String) -> Unit)? = null)

    fun pageUp()

    fun pageDown()

    fun reload()
}