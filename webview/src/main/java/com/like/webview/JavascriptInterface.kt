package com.like.webview

import android.util.Log
import android.webkit.JavascriptInterface
import com.tencent.smtt.sdk.WebView

/**
 * android 和 js 相互调用的帮助类。
 */
class JavascriptInterface(private val webView: WebView) {
    private val androidMethodMap = mutableMapOf<String, (String) -> String>()

    /**
     * 注册 android 方法。这个方法会被JS调用。
     * js 调用如下：
     * function callAndroid(){// 方法名字随意
     *      var user = {};
     *      user.name= "like";
     *      user.age = 36;
     *      var str=window.androidAPI.callAndroidMethod("androidMethodName",JSON.stringify(user));// window.androidAPI.callAndroidMethod(methodName, paramsJsonString) 为固定格式
     *      alert("调用了Android的callAndroidMethod方法，返回值：" + str);
     * }
     *
     * @param methodName    android 方法的名字
     * @param method        android 方法。传入方法的参数是 String 类型，如果是多个参数，可以用 json 字符串；返回值也是 String 类型的
     */
    fun registerAndroidMethodForJSCall(methodName: String, method: (String) -> String) {
        androidMethodMap[methodName] = method
    }

    /**
     * 取消注册的 android 方法
     */
    fun unRegisterAndroidMethodForJSCall(methodName: String) {
        if (androidMethodMap.containsKey(methodName))
            androidMethodMap.remove(methodName)
    }

    /**
     * 清空注册的 android 方法
     */
    fun clearAndroidMethodForJSCall() {
        androidMethodMap.clear()
    }

    /**
     * js 调用 android 方法
     */
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

    /**
     * android 调用 js 方法
     *
     * @param jsMethodName      js 方法的名字
     * @param paramsJsonString  js 方法的参数
     * @param callback          回调方法，用于处理 js 方法返回的 String 类型的结果。
     */
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