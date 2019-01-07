package com.like.webview.sample

import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.like.webview.X5Listener
import com.like.webview.X5WebView
import com.like.webview.sample.databinding.ActivityWebviewBinding
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView

class WebViewActivity : AppCompatActivity() {
    private val mBinding: ActivityWebviewBinding by lazy {
        DataBindingUtil.setContentView<ActivityWebviewBinding>(this, R.layout.activity_webview)
    }
    private val x5WebView: X5WebView by lazy {
        mBinding.webView.x5WebView
    }
    private val webView: WebView by lazy {
        x5WebView.tencentWebView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView.addJavascriptInterface(JavascriptObject(webView), "androidAPI")
        val url = "file:///android_asset/index.html"
//        val url = "www.baidu.com"
        webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE// 支持微信H5支付
        x5WebView.mListener = object : X5Listener {
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
        }

        webView.loadUrl(url)
    }

    fun pageUp(view: View) {
        webView.pageUp(true)
    }

    fun pageDown(view: View) {
        webView.pageDown(true)
    }

    fun refresh(view: View) {
        webView.reload()
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
    }
}