package com.like.webview

import android.graphics.PixelFormat
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

/**
 * 对 [WebViewFragment] 的封装
 */
abstract class BaseWebViewActivity : AppCompatActivity() {
    var webViewFragment: WebViewFragment? = null
        private set

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
        webViewFragment?.let {
            Log.e("Logger", "intWebViewFragment")
            intWebViewFragment(it)
        }
    }

    /**
     * [webViewFragment]的容器资源 id
     */
    abstract fun getFragmentHolderResId(): Int

    /**
     * 在此方法中进行[webViewFragment]相关的设置
     */
    abstract fun intWebViewFragment(webViewFragment: WebViewFragment)

}
