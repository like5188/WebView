package com.like.webview.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.like.webview.ext.SimpleWebViewActivity
import com.like.webview.sample.viewpager.ViewPagerActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun simpleWebViewActivity(view: View) {
        SimpleWebViewActivity.start(this, "https://cn.bing.com/")
    }

    fun baseWebViewFragment(view: View) {
        startActivity(Intent(this, ViewPagerActivity::class.java))
    }

    fun baseWebViewActivity(view: View) {
        startActivity(Intent(this, WebViewActivity::class.java))
    }

}
