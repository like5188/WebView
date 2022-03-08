package com.like.webview.sample

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.like.common.util.Logger
import com.like.webview.BaseWebViewActivity
import com.like.webview.WebViewFragmentConfig
import com.like.webview.X5ListenerAdapter
import com.like.webview.sample.databinding.ActivityWebviewFragmentBinding
import com.tencent.smtt.sdk.WebView
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL

class WebViewFragmentActivity : BaseWebViewActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityWebviewFragmentBinding>(this, R.layout.activity_webview_fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
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
        localStorageMap["kwcw4-h5"] = "123123"
        x5Listener = object : X5ListenerAdapter() {
            override fun onReceivedIcon(webView: WebView?, icon: Bitmap?) {
                mBinding.ivIcon.setImageBitmap(icon)
            }

            override fun onReceivedTitle(webView: WebView?, title: String?) {
                mBinding.tvTitle.text = title
            }
        }
    }

    fun getCookies(view: View) {
        val url = "http://192.168.0.188/my/userInfo?client=Android"
        Log.e("Logger", webViewFragment?.getCookie(URL(url).host) ?: "")
    }

    fun clearCookies(view: View) {
        webViewFragment?.clearCookies()
    }

    fun getLocalStorage(view: View) {
        lifecycleScope.launch {
            Log.e("Logger", webViewFragment?.getLocalStorageItem("kwcw4-h5") ?: "")
        }
    }

    fun clearLocalStorage(view: View) {
        webViewFragment?.clearLocalStorage()
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
