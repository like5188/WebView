package com.like.webview

import android.graphics.PixelFormat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * 对 [WebViewFragment] 的封装
 */
abstract class BaseWebViewActivity : AppCompatActivity() {
    var mWebViewFragment: WebViewFragment? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 网页中的视频，上屏幕的时候，可能出现闪烁的情况，需要如下设置：Activity在onCreate时需要设置:
        window.setFormat(PixelFormat.TRANSLUCENT)

        if (mWebViewFragment == null) {
            supportFragmentManager.beginTransaction().apply {
                WebViewFragment().apply {
                    add(getFragmentHolderResId(), this)
                    mWebViewFragment = this
                }
            }.commitNow()
        }
    }

    abstract fun getFragmentHolderResId(): Int

}
