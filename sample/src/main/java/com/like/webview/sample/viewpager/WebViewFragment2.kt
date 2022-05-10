package com.like.webview.sample.viewpager

import com.like.webview.ext.BaseWebViewFragment
import com.like.webview.ext.WebViewConfig
import com.tencent.smtt.sdk.WebView

class WebViewFragment2 : BaseWebViewFragment() {
    override fun getWebViewConfig(webView: WebView): WebViewConfig {
        return WebViewConfig().apply {
            url = "https://cn.bing.com/"
        }
    }
}