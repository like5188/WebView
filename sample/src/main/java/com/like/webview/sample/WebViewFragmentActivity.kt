package com.like.webview.sample

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.like.webview.BaseWebViewActivity
import com.like.webview.WebViewFragmentConfig
import com.like.webview.X5ListenerAdapter
import com.like.webview.sample.databinding.ActivityWebviewFragmentBinding
import com.tencent.smtt.sdk.CookieManager
import com.tencent.smtt.sdk.WebView


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
        // 注意：cookie 要在 WebView 的 setting 设置完之后调用，否则无效。
        try {
            CookieManager.getInstance().apply {
                setAcceptCookie(true)
                removeAllCookie()
                setCookie(
                    "http://192.168.0.188",
                    "source={\"token\":\"cDaxVHpdMjNvAueIQ3R56svRryk\",\"refreshToken\":\"X8rweH2v7PvsSEr7rEC9ES8vedQ\",\"tokenArray\":{\"tel\":\"13399857800\",\"type\":3,\"source\":\"Pc\"},\"oldtoken\":\"IPNPx563jakGvZrej2FP8IA7yQA58Coh\"}"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        webViewFragment?.loadUrl("http://192.168.0.188/my/userInfo?client=android")
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
//        url = "file:///android_asset/index.html"
        javascriptInterfaceMap["appKcwc"] = JsUtils.JavascriptInterface()
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
                webViewFragment?.localStorage("kwcw4-h5", "1111111111")
            }

        }
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

    fun callGetLocalStorage(view: View) {
        JsUtils.callGetLocalStorage(webViewFragment)
    }

    fun callJSMethodWithParams(view: View) {
        JsUtils.callJSMethodWithParams(webViewFragment)
    }

}
