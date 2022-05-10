package com.like.webview.ext

import com.like.webview.core.X5Listener
import com.tencent.smtt.export.external.interfaces.*
import com.tencent.smtt.sdk.WebView

class WebViewConfig {
    /**
     * [url] 的加载时机是在第一次 [onResume] 时。如果不传此参数，可以自行调用 [WebView.loadUrl] 方法。
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
