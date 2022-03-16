package com.like.webview.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.like.webview.sample.viewpager.ViewPagerActivity
import com.like.webview.ext.SimpleWebViewActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun simpleWebViewActivity(view: View) {
        SimpleWebViewActivity.start(this, "https://v.qq.com/x/page/c0041x3xse6.html")
    }

    fun webViewActivity(view: View) {
        startActivity(Intent(this, WebViewFragmentActivity::class.java))
    }

    fun viewPager(view: View) {
        startActivity(Intent(this, ViewPagerActivity::class.java))
    }

}
