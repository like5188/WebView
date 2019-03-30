package com.like.webview

import android.util.Log
import android.webkit.JavascriptInterface
import com.tencent.smtt.sdk.WebView

class JavascriptInterface(private val webView: WebView) {
    private val androidMethodMap = mutableMapOf<String, (String) -> String>()

    /**
     * 注册方法。这个方法会被JS调用。
     */
    fun registerAndroidMethodForJSCall(methodName: String, method: (String) -> String) {
        androidMethodMap[methodName] = method
    }

    // js调用android方法
    @JavascriptInterface // API17及以上的版本中，需要此注解才能调用下面的方法
    fun callAndroidMethod(methodName: String, paramsJsonString: String): String {
        return if (androidMethodMap.containsKey(methodName)) {
            val result = androidMethodMap[methodName]?.invoke(paramsJsonString) ?: ""
            Log.d("JavascriptInterface", "js调用了android方法，方法名：$methodName，传递的参数：paramsJsonString=$paramsJsonString，android方法的返回值：$result")
            result
        } else {
            Log.e("JavascriptInterface", "js调用了android方法错误，没有注册该方法。方法名：$methodName，传递的参数：paramsJsonString=$paramsJsonString")
            ""
        }
    }

    // android调用js方法
    fun callJsMethod(jsMethodName: String, paramsJsonString: String? = null, callback: ((String) -> Unit)? = null) {
        if (jsMethodName.isEmpty()) return
        val jsString = if (paramsJsonString.isNullOrEmpty()) {
            "javascript:$jsMethodName()"
        } else {
            "javascript:$jsMethodName('$paramsJsonString')"
        }
//        webView.post { webView.loadUrl(jsString) }// Ui线程
        // a)比第一种方法效率更高、使用更简洁，因为该方法的执行不会使页面刷新，而第一种方法（loadUrl ）的执行则会。
        // b)Android 4.4 后才可使用
        webView.evaluateJavascript(jsString) {
            Log.v("JavascriptInterface", "android调用js方法，方法名：$jsMethodName，传递的参数：paramsJsonString=$paramsJsonString，js方法的返回值：$it")
            callback?.invoke(it)
        }
    }
}