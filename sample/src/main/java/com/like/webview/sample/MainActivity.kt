package com.like.webview.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.like.webview.WebViewActivity
import com.like.webview.sample.viewpager.ViewPagerActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun fragment(view: View) {
        startActivity(Intent(this, WebViewFragmentActivity::class.java))
    }

    fun activity(view: View) {
        WebViewActivity.start(this, "https://www.sina.com.cn/", false)
    }

    fun viewPager(view: View) {
        startActivity(Intent(this, ViewPagerActivity::class.java))
    }

}
