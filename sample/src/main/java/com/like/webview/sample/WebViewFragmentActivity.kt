package com.like.webview.sample

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.like.common.util.Logger
import com.like.common.util.selectSinglePhoto
import com.like.common.util.uploadPath
import com.like.webview.listener.X5ListenerAdapter
import com.like.webview.sample.databinding.ActivityWebviewFragmentBinding
import com.like.webview.ui.BaseWebViewActivity
import com.like.webview.ui.WebViewFragmentConfig
import com.like.webview.util.*
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

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
//        url = "http://192.168.0.188/my/userInfo"
        javascriptInterfaceMap["appKcwc"] = MyJavascriptInterface()
        cookieMap["cookieKey1"] = arrayOf("1=1", "2=2")
        cookieMap["cookieKey2"] = arrayOf("3=3", "4=4", "5=5")
        cookieMap["192.168.0.188"] = arrayOf("mechine_type=android")
        localStorageMap["localStorageKey1"] = "1111111111"
        localStorageMap["localStorageKey2"] = "2222"
        x5Listener = object : X5ListenerAdapter() {
            override fun onReceivedIcon(webView: WebView?, icon: Bitmap?) {
                mBinding.ivIcon.setImageBitmap(icon)
            }

            override fun onReceivedTitle(webView: WebView?, title: String?) {
                mBinding.tvTitle.text = title
            }

            override fun onShowFileChooser(
                webView: WebView?,
                callback: ValueCallback<Array<Uri>>?,
                params: WebChromeClient.FileChooserParams?
            ): Boolean {
                lifecycleScope.launch {
                    //选择照片
                    val result = selectSinglePhoto()
                    val compressPath = result?.compressPath
                    val uploadPath = result?.uploadPath
                    if (compressPath.isNullOrEmpty() || uploadPath.isNullOrEmpty()) {
                        callback?.onReceiveValue(null)
                    } else {
                        callback?.onReceiveValue(arrayOf(Uri.fromFile(File(uploadPath))))
                    }
                }
                return true
            }
        }
    }

    fun getCookies(view: View) {
        addCookies(mapOf("cookieKey1" to arrayOf("3=3", "3=4")))
        Logger.e(getCookie("cookieKey1"))
        Logger.e(getCookie("cookieKey2"))
        Logger.e(getCookie("192.168.0.188"))
        clearCookies()
        addCookies(mapOf("cookieKey1" to arrayOf("7=7", "8=8")))
        Logger.e(getCookie("cookieKey1"))
        Logger.e(getCookie("cookieKey2"))
        Logger.e(getCookie("192.168.0.188"))
    }

    fun getLocalStorages(view: View) {
        x5WebView?.addLocalStorages(mapOf("localStorageKey1" to "3"))
        lifecycleScope.launch {
            Logger.e(x5WebView?.getLocalStorage("localStorageKey1"))
            Logger.e(x5WebView?.getLocalStorage("localStorageKey2"))
            x5WebView?.clearLocalStorages()
            x5WebView?.addLocalStorages(mapOf("localStorageKey1" to "1"))
            Logger.e(x5WebView?.getLocalStorage("localStorageKey1"))
            Logger.e(x5WebView?.getLocalStorage("localStorageKey2"))
        }
    }

    fun pageUp(view: View) {
        x5WebView?.pageUp(true)
    }

    fun pageDown(view: View) {
        x5WebView?.pageDown(true)
    }

    fun reload(view: View) {
        x5WebView?.reload()
    }

    fun callJSMethodWithParams(view: View) {
        val params = JSONObject()
        params.put("name", "like")
        params.put("age", 1)
        lifecycleScope.launch {
            val result = x5WebView?.callJsMethod(
                "jsMethodNameWithParams",
                params.toString()
            )
            Logger.d("callJSMethodWithParams 返回值：$result")
        }
    }

}
