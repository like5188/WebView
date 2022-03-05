package com.like.webview.sample

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.like.common.util.Logger
import com.like.webview.BaseWebViewActivity
import com.like.webview.X5Listener
import com.like.webview.sample.databinding.ActivityWebviewFragmentBinding
import com.tencent.smtt.sdk.CookieManager
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

    override fun onStart() {
        super.onStart()
        initWebViewFragment()
    }

    private fun initWebViewFragment() {
        mWebViewFragment?.addJavascriptInterface(JavascriptInterface(), "appKcwc")
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.removeAllCookie()
        cookieManager.setCookie("http://car1.i.cacf.cn", "mechine_type=android")
        mWebViewFragment?.setListener(object : X5Listener {
            override fun onReceivedIcon(webView: WebView?, icon: Bitmap?) {
                mBinding.ivIcon.setImageBitmap(icon)
            }

            override fun onReceivedTitle(webView: WebView?, title: String?) {
                mBinding.tvTitle.text = title
            }

            override fun onProgressChanged(webView: WebView?, progress: Int?) {
            }

            override fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?) {
            }

            override fun onPageFinished(webView: WebView?, url: String?) {
            }

            override fun onReceivedError(webView: WebView?) {
            }
        })
    }

    fun pageUp(view: View) {
        mWebViewFragment?.pageUp()
    }

    fun pageDown(view: View) {
        mWebViewFragment?.pageDown()
    }

    fun reload(view: View) {
        mWebViewFragment?.reload()
    }

    fun callJSMethod(view: View) {
        try {
            val params = JSONObject()
            params.put("name", "like1")
            params.put("age", 22)
            mWebViewFragment?.callJsMethod(
                "jsMethodName",
                params.toString()
            ) {
                Logger.d("callJsMethod 返回值：$it")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private class JavascriptInterface {
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

            @android.webkit.JavascriptInterface
            fun goBack() {
                Logger.d("js调用了goBack方法")
            }

            @android.webkit.JavascriptInterface
            fun login() {
                Logger.d("js调用了login方法")
            }

            @android.webkit.JavascriptInterface
            fun login(a: String) {
                Logger.d("js调用了login方法，参数：$a")
            }
        }
    }
}
