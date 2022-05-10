package com.like.webview.ext

import android.graphics.PixelFormat
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.like.webview.R
import com.like.webview.databinding.ActivityWebviewBinding
import com.tencent.smtt.sdk.WebView

abstract class BaseWebViewActivity : FragmentActivity() {
    private val mBinding: ActivityWebviewBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_webview)
    }
    private val webViewHelper: WebViewHelper by lazy {
        WebViewHelper()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 网页中的视频，上屏幕的时候，可能出现闪烁的情况，需要如下设置：Activity在onCreate时需要设置:
        window.setFormat(PixelFormat.TRANSLUCENT)
        webViewHelper.onCreate(this) {
            getWebViewConfig(it)
        }
        mBinding.fl.removeAllViews()
        mBinding.fl.addView(webViewHelper.x5WebViewWithProgressBar)
    }

    override fun onPause() {
        super.onPause()
        webViewHelper.onPause()
    }

    override fun onResume() {
        super.onResume()
        webViewHelper.onResume()
    }

    override fun onDestroy() {
        webViewHelper.onDestroy()
        super.onDestroy()
    }

    abstract fun getWebViewConfig(webView: WebView): WebViewConfig

}
