package com.like.webview.sample

import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.webview.JavascriptInterface
import com.like.webview.X5Listener
import com.like.webview.X5ProgressBarWebView
import com.like.webview.sample.databinding.ActivityTestBinding
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import org.json.JSONObject

class TestActivity : AppCompatActivity() {
    private val mBinding: ActivityTestBinding by lazy {
        DataBindingUtil.setContentView<ActivityTestBinding>(this, R.layout.activity_test)
    }
    private val mX5ProgressBarWebView: X5ProgressBarWebView by lazy {
        mBinding.webView
    }
    private val mWebView: WebView by lazy {
        mX5ProgressBarWebView.getWebView()
    }
    private val mJavascriptInterface by lazy { JavascriptInterface() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 网页中的视频，上屏幕的时候，可能出现闪烁的情况，需要如下设置：Activity在onCreate时需要设置:
        window.setFormat(PixelFormat.TRANSLUCENT)
        mWebView.addJavascriptInterface(mJavascriptInterface, "androidAPI")
        mJavascriptInterface.registerAndroidMethodForJSCall("androidMethodName") {
            try {
                val jsonObject = JSONObject(it)
                val name = jsonObject.optString("name")
                val age = jsonObject.optInt("age")
                Log.d("WebViewActivity", "androidMethodName name=$name age=$age")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            "js调用android方法成功"
        }
        val url = "file:///android_asset/index.html"
//        val url = "http://www.sohu.com/"
        mWebView.settings.cacheMode = WebSettings.LOAD_NO_CACHE// 支持微信H5支付
        mX5ProgressBarWebView.setListener(object : X5Listener {
            override fun onReceivedIcon(webView: WebView?, icon: Bitmap?) {
                mBinding.ivIcon.setImageBitmap(icon)
            }

            override fun onReceivedTitle(webView: WebView?, title: String?) {
                if (title != null && title.length > 6)
                    mBinding.tvTitle.text = "${title.subSequence(0, 6)}..."
                else
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

        mWebView.loadUrl(url)
    }

    fun callJSMethod(view: View) {
        try {
            val params = JSONObject()
            params.put("name", "like1")
            params.put("age", 22)
            mJavascriptInterface.callJsMethod(mWebView, "jsMethodName", params.toString()) {
                Log.d("WebViewActivity", "callJsMethod 返回值：$it")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pageUp(view: View) {
        mWebView.pageUp(true)
    }

    fun pageDown(view: View) {
        mWebView.pageDown(true)
    }

    fun refresh(view: View) {
        mWebView.reload()
    }

    override fun onDestroy() {
        super.onDestroy()
        mWebView.destroy()
        mJavascriptInterface.clearAndroidMethodForJSCall()
    }

}
