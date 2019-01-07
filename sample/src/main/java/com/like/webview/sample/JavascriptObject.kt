package com.like.webview.sample

import android.webkit.JavascriptInterface
import com.tencent.smtt.sdk.WebView

class JavascriptObject(private val webView: WebView) {

    // js调用android方法
    @JavascriptInterface // API17及以上的版本中，需要此注解才能调用下面的方法
    fun gotoOrderConfirm(goods_list: String, activity_type: Int) {
    }

    // android调用js方法
    @JavascriptInterface
    fun updateImage(position: Int, imagePath: String) {
        webView.post { webView.loadUrl("javascript:serverid($position,'$imagePath')") }
    }
}