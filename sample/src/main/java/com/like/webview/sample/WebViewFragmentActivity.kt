package com.like.webview.sample

import android.graphics.Bitmap
import android.os.Bundle
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
        javascriptInterfaceMap["appKcwc"] = MyJavascriptInterface()
        cookieMap["cookieKey1"] = arrayOf("1=1", "2=2")
        cookieMap["cookieKey2"] = arrayOf("3=3", "4=4", "5=5")
        localStorageMap["localStorageKey1"] = "1111111111"
        localStorageMap["localStorageKey2"] = "2222"
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
        Logger.printMap(webViewFragment?.getCookies())
    }

    fun getLocalStorages(view: View) {
        lifecycleScope.launch {
            Logger.printMap(webViewFragment?.getLocalStorages())
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
