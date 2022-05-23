package com.like.webview.core

import com.tencent.smtt.sdk.CookieManager
import com.tencent.smtt.sdk.WebView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * android 调用 js 方法
 *
 * @param methodName        js 方法的名字
 * @param params            js 方法的参数
 */
suspend fun WebView.callJsMethod(methodName: String, params: Any? = null): String? = withContext(Dispatchers.Main) {
    suspendCoroutine { continuation ->
        if (methodName.isEmpty()) {
            continuation.resume(null)
            return@suspendCoroutine
        }
        val js = "javascript:$methodName($params)"
//        webView.post { webView.loadUrl(jsString) }// Ui线程
        // a)比第一种方法效率更高、使用更简洁，因为该方法的执行不会使页面刷新，而第一种方法（loadUrl ）的执行则会。
        // b)Android 4.4 后才可使用
        evaluateJavascript(js) {
            continuation.resume(it)
        }
    }
}

/**
 * 往浏览器的 localStorage 中写入数据。
 * @param map   key：相同的 key 会覆盖；
 */
fun WebView.addLocalStorages(map: Map<String, Any?>) {
    map.forEach {
        evaluateJavascript("window.localStorage.setItem('${it.key}',${it.value});", null)
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
 * @param map
 * key：相同的 key 会追加；
 * value：字符串数组，其中每个字符串的格式为 "key=value"，相同的key会覆盖；如果 "key=value" 都相同，则会跳过不重复设置。
 */
fun addCookies(map: Map<String, Array<String>>) {
    CookieManager.getInstance().apply {
        if (!acceptCookie()) {
            setAcceptCookie(true)
        }
        // 注意：这里不能直接使用 com.tencent.smtt.sdk.CookieManager.setCookies 方法，因为经常会失败。要使用 setCookie 方法多次设置。
        map.forEach { entry ->
            val cookieList = getCookieList(entry.key)
            entry.value.forEach { value ->
                if (!cookieList.contains(value)) {
                    setCookie(entry.key, value)
                }
            }
        }
    }
}

fun getCookie(key: String): String {
    return CookieManager.getInstance().getCookie(key) ?: ""
}

fun getCookieList(key: String): List<String> {
    return getCookie(key).split("; ")
}

fun clearCookies() {
    CookieManager.getInstance().removeAllCookies(null)
}
