package com.like.webview.sample.viewpager

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.like.common.util.Logger
import com.like.common.util.dp
import com.like.common.util.selectSinglePhoto
import com.like.common.util.uploadPath
import com.like.webview.core.*
import com.like.webview.ext.BaseWebViewFragment
import com.like.webview.ext.WebViewConfig
import com.like.webview.sample.MyJavascriptInterface
import com.like.webview.sample.R
import com.like.webview.sample.databinding.FragmentWebviewBinding
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

class WebViewFragment1 : BaseWebViewFragment() {
    private lateinit var mBinding: FragmentWebviewBinding
    private lateinit var x5WebView: WebView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_webview, container, true)
        mBinding.flWebView.removeAllViews()
        mBinding.flWebView.addView(x5WebView)
        mBinding.btnPageUp.setOnClickListener {
            x5WebView.pageUp(true)
        }
        mBinding.btnPageDown.setOnClickListener {
            x5WebView.pageDown(true)
        }
        mBinding.btnReload.setOnClickListener {
            x5WebView.reload()
        }
        mBinding.btnCallJSMethodWithParams.setOnClickListener {
            val params = JSONObject()
            params.put("name", "like")
            params.put("age", 1)
            lifecycleScope.launch {
                val result = x5WebView.callJsMethod(
                    "jsMethodNameWithParams",
                    params
                )
                Logger.d("callJSMethodWithParams 返回值：$result")
            }
        }
        mBinding.btnGetCookies.setOnClickListener {
            Logger.e(getCookie("cookieKey1"))
            Logger.e(getCookie("cookieKey2"))
            Logger.e(getCookie("192.168.0.188"))
            addCookies(mapOf("cookieKey1" to arrayOf("1=1", "2=3", "4")))
            Logger.e(getCookie("cookieKey1"))
            Logger.e(getCookie("cookieKey2"))
            Logger.e(getCookie("192.168.0.188"))
            clearCookies()
            addCookies(mapOf("cookieKey1" to arrayOf("7=7", "8=8")))
            Logger.e(getCookie("cookieKey1"))
            Logger.e(getCookie("cookieKey2"))
            Logger.e(getCookie("192.168.0.188"))
        }
        mBinding.btnGetLocalStorages.setOnClickListener {
            x5WebView.addLocalStorages(mapOf("localStorageKey1" to null))
            lifecycleScope.launch {
                Logger.e(x5WebView.getLocalStorage("localStorageKey1"))
                Logger.e(x5WebView.getLocalStorage("localStorageKey2"))
                x5WebView.clearLocalStorages()
                x5WebView.addLocalStorages(mapOf("localStorageKey1" to "1"))
                Logger.e(x5WebView.getLocalStorage("localStorageKey1"))
                Logger.e(x5WebView.getLocalStorage("localStorageKey2"))
            }
        }
        return mBinding.root
    }

    override fun getWebViewConfig(webView: WebView): WebViewConfig {
        return WebViewConfig().apply {
            this@WebViewFragment1.x5WebView = webView
            url = "file:///android_asset/index.html"
//            url = "http://kcwc.m.cacf.cn/static-kcwc2/my/userInfo"
            progressBarHeight = 3f.dp
            javascriptInterfaceMap["appKcwc"] = MyJavascriptInterface(webView)
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

                override fun onReceivedSslError(webView: WebView?, handler: SslErrorHandler?, error: SslError?): Boolean? {
                    return true
                }
            }
        }
    }

}