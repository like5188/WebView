package com.like.webview.sample

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.like.webview.BaseWebViewActivity
import com.like.webview.WebViewFragmentConfig
import com.like.webview.X5ListenerAdapter
import com.like.webview.sample.databinding.ActivityWebviewFragmentBinding
import com.tencent.smtt.sdk.WebView
import org.json.JSONObject

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
//        url = "file:///android_asset/index.html"
//        javascriptInterfaceMap["appKcwc"] = JsUtils.JavascriptInterface()
        url = "http://kcwc.m.cacf.cn/static-kcwc2/my/userInfo?client=android"
        x5Listener = object : X5ListenerAdapter() {
            override fun onReceivedIcon(webView: WebView?, icon: Bitmap?) {
                mBinding.ivIcon.setImageBitmap(icon)
            }

            override fun onReceivedTitle(webView: WebView?, title: String?) {
                mBinding.tvTitle.text = title
            }

            override fun onPageFinished(webView: WebView?, url: String?) {
                val j = JSONObject()
                j.put(
                    "source",
                    "{\"token\":\"Xbu-v3tp7eMD4n1hEHlYi3nYTfU\",\"refreshToken\":\"s1-swTpB2FBH-CabD33zoJiMZ1I\",\"tokenArray\":{\"tel\":\"13399857800\",\"type\":3,\"source\":\"Pc\"},\"oldtoken\":\"IPNPx563jakGvZrej2FP8IA7yQA58Coh\"}"
                )
                webViewFragment?.localStorage("kwcw4-h5", j.toString())
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

    fun callJSMethod(view: View) {
        JsUtils.callJSMethod(webViewFragment)
    }

    fun callJSMethodWithParams(view: View) {
        JsUtils.callJSMethodWithParams(webViewFragment)
    }

}
