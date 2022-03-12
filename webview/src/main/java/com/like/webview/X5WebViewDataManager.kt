package com.like.webview

import com.tencent.smtt.sdk.CookieManager
import com.tencent.smtt.sdk.WebView
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * android 调用 js 方法
 *
 * @param methodName        js 方法的名字
 * @param params            js 方法的参数
 * @param callback          回调方法，用于处理 js 方法返回的 String 类型的结果。
 */
fun WebView.callJsMethod(methodName: String, params: String? = null, callback: ((String?) -> Unit)? = null) {
    if (methodName.isEmpty()) return
    val jsString = if (params.isNullOrEmpty()) {
        "javascript:$methodName()"
    } else {
        "javascript:$methodName('$params')"
    }
//        webView.post { webView.loadUrl(jsString) }// Ui线程
    // a)比第一种方法效率更高、使用更简洁，因为该方法的执行不会使页面刷新，而第一种方法（loadUrl ）的执行则会。
    // b)Android 4.4 后才可使用
    evaluateJavascript(jsString) {
        callback?.invoke(it)
    }
}

/**
 * 往浏览器的 localStorage 中写入数据。
 * @param map   key：相同的 key 会覆盖；
 */
fun WebView.addLocalStorages(map: Map<String, String>) {
    map.forEach {
        evaluateJavascript("window.localStorage.setItem('${it.key}','${it.value}');", null)
    }
}

suspend fun WebView.getLocalStorage(key: String): String? = suspendCoroutine { cont ->
    evaluateJavascript("window.localStorage.getItem('$key');") {
        cont.resume(it)
    }
}

fun WebView.clearLocalStorages() {
    evaluateJavascript("window.localStorage.clear();", null)
}

/**
 * 添加 JavascriptInterface
 * @param map   key：相同的 key 会覆盖；
 */
fun WebView.addJavascriptInterfaces(map: Map<String, Any>) {
    map.forEach {
        addJavascriptInterface(it.value, it.key)
    }
}

/**
 * 添加 cookies
 * 注意：必须要在 WebView 的 settings 设置完之后调用才有效。
 * @param map   key：相同的 key 会追加；value：字符串数组，其中每个字符串的格式为 "key=value"，相同的key会覆盖；
 */
fun addCookies(map: Map<String, Array<String>>) {
    CookieManager.getInstance().apply {
        if (!acceptCookie()) {
            setAcceptCookie(true)
        }
        setCookies(map)
    }
}

fun getCookie(key: String): String {
    return CookieManager.getInstance().getCookie(key) ?: ""
}

fun clearCookies() {
    CookieManager.getInstance().removeAllCookies(null)
}
