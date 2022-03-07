package com.like.webview.sample

import android.util.Log
import com.like.common.util.Logger
import com.like.webview.WebViewFragment
import org.json.JSONObject

/*
js 与 android 交互的辅助工具类。配合 index.html 使用。
 */

fun addJavascriptInterface(webViewFragment: WebViewFragment?) {
    webViewFragment?.addJavascriptInterface(JavascriptInterface(), "appKcwc")
}

fun callJSMethod(webViewFragment: WebViewFragment?) {
    webViewFragment?.callJsMethod("jsMethodName") {
        Logger.d("callJsMethod 返回值：$it")
    }
}

fun callJSMethodWithParams(webViewFragment: WebViewFragment?) {
    webViewFragment ?: return
    val params = JSONObject()
    params.put("name", "like")
    params.put("age", 1)
    webViewFragment.callJsMethod(
        "jsMethodNameWithParams",
        params.toString()
    ) {
        Logger.d("callJSMethodWithParams 返回值：$it")
    }
}

private class JavascriptInterface {
    @android.webkit.JavascriptInterface// API17及以上的版本中，需要此注解才能调用下面的方法
    fun androidMethod(params: String): String {
        try {
            val jsonObject = JSONObject(params)
            val name = jsonObject.optString("name")
            val age = jsonObject.optInt("age")
            Log.d("WebViewActivity", "androidMethod name=$name age=$age")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "js 调用 android 的 androidMethod 方法成功"
    }
}