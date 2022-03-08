package com.like.webview.sample

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.like.common.util.Logger
import com.like.webview.BaseWebViewActivity
import com.like.webview.WebViewFragmentConfig
import com.like.webview.X5ListenerAdapter
import com.like.webview.sample.databinding.ActivityWebviewFragmentBinding
import com.tencent.smtt.sdk.CookieManager
import com.tencent.smtt.sdk.WebView
import org.json.JSONObject
import java.net.URL

class WebViewFragmentActivity : BaseWebViewActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityWebviewFragmentBinding>(this, R.layout.activity_webview_fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        Log.e("Logger", "WebViewFragmentActivity onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.e("Logger", "WebViewFragmentActivity onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.e("Logger", "WebViewFragmentActivity onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.e("Logger", "WebViewFragmentActivity onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.e("Logger", "WebViewFragmentActivity onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("Logger", "WebViewFragmentActivity onDestroy")
    }

    override fun getFragmentHolderResId(): Int {
        return R.id.fragment_holder
    }

    override fun getWebViewFragmentConfig(): WebViewFragmentConfig = WebViewFragmentConfig().apply {
        url = "file:///android_asset/index.html"
//        url = "http://192.168.0.188/my/userInfo?client=Android"
        javascriptInterfaceMap["appKcwc"] = MyJavascriptInterface()
        cookieMap[URL(url).host] =
            "source={\"token\":\"JRY2j000Ybt2UNE7YcXCgZZqfp0\",\"refreshToken\":\"6Teb3Ozkb8B_MAKaGr0MjxIAtZ0\",\"tokenArray\":{\"tel\":\"13399857800\",\"type\":3,\"source\":\"Pc\"},\"oldtoken\":\"IPNPx563jakGvZrej2FP8IA7yQA58Coh\"}"
        x5Listener = object : X5ListenerAdapter() {
            override fun onReceivedIcon(webView: WebView?, icon: Bitmap?) {
                mBinding.ivIcon.setImageBitmap(icon)
            }

            override fun onReceivedTitle(webView: WebView?, title: String?) {
                mBinding.tvTitle.text = title
            }

            override fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(webView, url, favicon)
                // localStorage
                webViewFragment?.setLocalStorageItem("kwcw4-h5", "1111111111")
            }

        }
    }

    fun getCookies(view: View) {
        val url = "http://192.168.0.188/my/userInfo?client=Android"
        Log.e("Logger", CookieManager.getInstance().getCookie(URL(url).host) ?: "")
    }

    fun clearCookies(view: View) {
        CookieManager.getInstance().removeAllCookies(null)
    }

    fun pageUp(view: View) {
        webViewFragment?.pageUp()
    }

    fun pageDown(view: View) {
        webViewFragment?.pageDown()
    }

    fun reload(view: View) {
        webViewFragment?.reload()
    }

    fun clearLocalStorage(view: View) {
        webViewFragment?.clearLocalStorage()
    }

    fun callGetLocalStorage(view: View) {
        webViewFragment?.callJsMethod("getLocalStorage")
    }

    fun callJSMethodWithParams(view: View) {
        val params = JSONObject()
        params.put("name", "like")
        params.put("age", 1)
        webViewFragment?.callJsMethod(
            "jsMethodNameWithParams",
            params.toString()
        ) {
            Logger.d("callJSMethodWithParams 返回值：$it")
        }
    }

}
