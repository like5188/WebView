package com.like.webview.sample

import com.like.webview.ext.BaseWebViewActivity
import com.like.webview.ext.WebViewConfig
import com.tencent.smtt.sdk.WebView

class WebViewActivity : BaseWebViewActivity() {

    override fun getWebViewConfig(webView: WebView): WebViewConfig = WebViewConfig().apply {
        url = "file:///android_asset/index.html"
        javascriptInterfaceMap["appKcwc"] = MyJavascriptInterface(webView)
    }

}
