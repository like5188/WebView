package com.like.webview.sample

import android.graphics.PixelFormat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.common.base.addFragments
import com.like.common.util.AutoWired
import com.like.common.util.injectForIntentExtras
import com.like.common.util.startActivity
import com.like.webview.sample.databinding.WebviewActivityWebviewBinding

/**
 * 对 [WebViewFragment] 的封装
 * 用于简单显示网页，而不进行交互。
 * 如果需要交互，请直接使用 [WebViewFragment]
 */
class WebViewActivity : AppCompatActivity() {
    @AutoWired
    var url: String? = null

    companion object {
        fun start(url: String?) {
            WebViewApplication.sInstance.startActivity<WebViewActivity>("url" to url)
        }
    }

    private val mBinding by lazy {
        DataBindingUtil.setContentView<WebviewActivityWebviewBinding>(
            this,
            R.layout.webview_activity_webview
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 网页中的视频，上屏幕的时候，可能出现闪烁的情况，需要如下设置：Activity在onCreate时需要设置:
        window.setFormat(PixelFormat.TRANSLUCENT)
        injectForIntentExtras()
        mBinding
        url?.let {
            addFragments(R.id.fragment_holder, 0, WebViewFragment(it))
        }
    }

}
