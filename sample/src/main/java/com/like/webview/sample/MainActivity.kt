package com.like.webview.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.like.webview.BaseWebViewActivity
import com.like.webview.SimpleWebViewActivity
import com.like.webview.sample.viewpager.ViewPagerActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun simpleWebViewActivity(view: View) {
        BaseWebViewActivity.start(this, SimpleWebViewActivity::class.java, "https://www.sina.com.cn/", progressBarHeight = 0f)
    }

    fun webViewActivity(view: View) {
        BaseWebViewActivity.start(this, WebViewFragmentActivity::class.java, "file:///android_asset/index.html")
    }

    fun viewPager(view: View) {
        startActivity(Intent(this, ViewPagerActivity::class.java))
    }

}
