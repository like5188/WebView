package com.like.webview.sample

import android.util.Log
import org.json.JSONObject

class MyJavascriptInterface {
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