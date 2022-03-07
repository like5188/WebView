package com.like.webview

import android.graphics.PixelFormat
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

/**
 * 对 [WebViewFragment] 的封装
 */
abstract class BaseWebViewActivity : FragmentActivity() {
    protected var webViewFragment: WebViewFragment? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 网页中的视频，上屏幕的时候，可能出现闪烁的情况，需要如下设置：Activity在onCreate时需要设置:
        window.setFormat(PixelFormat.TRANSLUCENT)
        if (webViewFragment == null) {
            supportFragmentManager.beginTransaction().apply {
                WebViewFragment(getWebViewFragmentConfig()).apply {
                    add(getFragmentHolderResId(), this)
                    webViewFragment = this
                }
            }.commit()
        }
    }

    /**
     * [webViewFragment]的容器资源 id
     */
    abstract fun getFragmentHolderResId(): Int

    abstract fun getWebViewFragmentConfig(): WebViewFragmentConfig

}
