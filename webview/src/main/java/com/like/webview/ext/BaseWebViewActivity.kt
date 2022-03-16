package com.like.webview.ext

import android.graphics.PixelFormat
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.tencent.smtt.sdk.WebView

/**
 * 对 [WebViewFragment] 的封装
 */
abstract class BaseWebViewActivity : FragmentActivity() {
    private val webViewFragment: WebViewFragment by lazy {
        WebViewFragment { webViewFragment, webView ->
            getWebViewFragmentConfig(webView)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 网页中的视频，上屏幕的时候，可能出现闪烁的情况，需要如下设置：Activity在onCreate时需要设置:
        window.setFormat(PixelFormat.TRANSLUCENT)
        supportFragmentManager.beginTransaction().apply {
            add(getFragmentHolderResId(), webViewFragment)
        }.commit()
    }

    /**
     * [webViewFragment]的容器资源 id
     */
    abstract fun getFragmentHolderResId(): Int

    abstract fun getWebViewFragmentConfig(webView: WebView): WebViewFragmentConfig

}
