package com.like.webview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.webview.databinding.ActivityWebviewBinding
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView

/**
 * 对 [WebViewFragment] 的封装
 * 用于简单显示网页，而不进行交互。
 * 如果需要交互，请直接使用 [WebViewFragment]
 */
class WebViewActivity : AppCompatActivity() {
    companion object {
        private const val KEY_URL = "key_url"
        fun start(context: Context, url: String?) {
            Intent(context, WebViewActivity::class.java).apply {
                if (context !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(KEY_URL, url)
                context.startActivity(this)
            }
        }
    }

    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityWebviewBinding>(
            this,
            R.layout.activity_webview
        )
    }
    private var mX5ProgressBarWebView: X5ProgressBarWebView? = null
    private var mWebView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 网页中的视频，上屏幕的时候，可能出现闪烁的情况，需要如下设置：Activity在onCreate时需要设置:
        window.setFormat(PixelFormat.TRANSLUCENT)
        mX5ProgressBarWebView = mBinding.x5ProgressBarWebView
        mWebView = mX5ProgressBarWebView?.getWebView()
        mWebView?.settings?.cacheMode = WebSettings.LOAD_NO_CACHE// 支持微信H5支付
        val url = intent.getStringExtra(KEY_URL)
        if (!url.isNullOrEmpty()) {
            mWebView?.loadUrl(url)
        }
    }

    override fun onPause() {
        super.onPause()
        mWebView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        mWebView?.onResume()
    }

    override fun onDestroy() {
        // 避免造成Fragment内存泄漏：http://42.193.188.64/articles/2021/08/09/1628511669976.html
        mX5ProgressBarWebView?.destroy()
        mX5ProgressBarWebView = null
        mWebView = null
        super.onDestroy()
    }

}
