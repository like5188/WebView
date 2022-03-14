package com.like.webview.ui

import android.graphics.PixelFormat
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.tencent.smtt.sdk.WebView

/**
 * 对 [WebViewFragment] 的封装
 */
abstract class BaseWebViewActivity : FragmentActivity() {
    private var webViewFragment: WebViewFragment? = null
    protected val x5WebView: WebView?
        get() = webViewFragment?.getX5WebView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 网页中的视频，上屏幕的时候，可能出现闪烁的情况，需要如下设置：Activity在onCreate时需要设置:
        window.setFormat(PixelFormat.TRANSLUCENT)
        if (webViewFragment == null) {
            supportFragmentManager.beginTransaction().apply {
                WebViewFragment().apply {
                    add(getFragmentHolderResId(), this)
                    webViewFragment = this
                }
            }.commit()
        }
    }

    override fun onStart() {
        super.onStart()
        // 此方法必须在 WebViewFragment.onCreateView() 之后调用，才能使得 WebViewFragment.init() 方法中的配置生效（x5WebViewWithErrorViewAndProgressBar?.apply{}）。
        webViewFragment?.init(getWebViewFragmentConfig())
    }

    /**
     * [webViewFragment]的容器资源 id
     */
    abstract fun getFragmentHolderResId(): Int

    abstract fun getWebViewFragmentConfig(): WebViewFragmentConfig

}
