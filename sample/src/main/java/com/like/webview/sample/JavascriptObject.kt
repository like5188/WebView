package com.like.webview.sample

import android.webkit.JavascriptInterface
import com.tencent.smtt.sdk.WebView

class JavascriptObject(private val webView: WebView) {

    // js调用android方法
    @JavascriptInterface // API17及以上的版本中，需要此注解才能调用下面的方法
    fun handleJS(goods_list: String, activity_type: Int): String {
        return ""
    }

    // android调用js方法
    fun callJS(position: Int, imagePath: String) {
        val jsString = "javascript:jsMethodName($position,'$imagePath')"
        webView.post { webView.loadUrl(jsString) }// Ui线程
        // a)比第一种方法效率更高、使用更简洁，因为该方法的执行不会使页面刷新，而第一种方法（loadUrl ）的执行则会。
        // b)Android 4.4 后才可使用
        webView.evaluateJavascript(jsString) {

        }
    }
}