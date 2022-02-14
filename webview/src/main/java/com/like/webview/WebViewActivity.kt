package com.like.webview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.webview.databinding.ActivityWebviewBinding

/**
 * 对 [WebViewFragment] 的封装
 * 用于简单显示网页，而不进行交互。
 * 如果需要交互，请直接使用 [WebViewFragment]
 */
class WebViewActivity : AppCompatActivity() {
    companion object {
        private const val KEY_URL = "key_url"
        private const val KEY_SHOW_PROGRESS = "key_show_progress"
        fun start(context: Context, url: String?, showProgress: Boolean = true) {
            Intent(context, WebViewActivity::class.java).apply {
                if (context !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(KEY_URL, url)
                putExtra(KEY_SHOW_PROGRESS, showProgress)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 网页中的视频，上屏幕的时候，可能出现闪烁的情况，需要如下设置：Activity在onCreate时需要设置:
        window.setFormat(PixelFormat.TRANSLUCENT)
        mBinding
        val showProgress = intent.getBooleanExtra(KEY_SHOW_PROGRESS, true)
        val url = intent.getStringExtra(KEY_URL)
        if (!url.isNullOrEmpty()) {
            supportFragmentManager.beginTransaction().apply {
                val tag = WebViewFragment::class.java.name
                // 防止重复添加
                if (supportFragmentManager.findFragmentByTag(tag) == null) {
                    add(R.id.fragment_holder, WebViewFragment(url, showProgress), tag)
                }
            }.commit()
        }
    }

}
