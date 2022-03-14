package com.like.webview.sample

import android.util.Log
import com.like.common.util.Logger
import org.json.JSONObject

class MyJavascriptInterface {
    // 注意线程。如果在方法中操作 WebView，就必须在主线程执行。
    // 否则会出现：java.lang.Throwable: A WebView method was called on thread 'JavaBridge'. All WebView methods must be called on the same thread. (Expected Looper Looper (main, tid 2) {75a8318} called on Looper (JavaBridge, tid 89) {424971f}, FYI main Looper is Looper (main, tid 2) {75a8318})
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

    @android.webkit.JavascriptInterface
    fun executeAppAction(param: String) {
        val paramJsonObject = JSONObject(param)
        val action = paramJsonObject.optString("action")
        val dataJsonObject = paramJsonObject.optJSONObject("data")
        Logger.d("$action $dataJsonObject")
        when (action) {
            "openAppPage" -> {
                val name = dataJsonObject?.optString("name")
                val params = dataJsonObject?.optJSONObject("params")
                when (name) {
                    "Login" -> {// 登录
                        Logger.e("登录")
                    }
                }
            }
        }
    }
}