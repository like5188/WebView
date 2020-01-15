package com.like.webview.sample

import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.launcher.ARouter
import com.like.common.base.BaseActivity
import com.like.webview.component.WebViewFragment
import com.like.webview.X5Listener
import com.like.webview.component.service.WebViewService
import com.like.webview.sample.databinding.ActivityTestBinding
import com.tencent.smtt.sdk.WebView
import org.json.JSONObject

class TestActivity : BaseActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityTestBinding>(this, R.layout.activity_test)
    }
    private var fragment: WebViewFragment? = null
    @Autowired
    @JvmField
    var mWebViewService: WebViewService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 网页中的视频，上屏幕的时候，可能出现闪烁的情况，需要如下设置：Activity在onCreate时需要设置:
        window.setFormat(PixelFormat.TRANSLUCENT)
        ARouter.getInstance().inject(this)
        mBinding
        val url = "file:///android_asset/index.html"
//        val url = "http://www.sohu.com/"
        mWebViewService?.getWebViewFragment(url)?.let {
            fragment = it as WebViewFragment
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_holder, it)
            }.commit()
        }
    }

    override fun onStart() {
        super.onStart()
        initWebViewFragment()
    }

    private fun initWebViewFragment() {
        fragment?.setInterfaceName("androidAPI")
        fragment?.registerAndroidMethodForJSCall("androidMethodName") {
            try {
                val jsonObject = JSONObject(it)
                val name = jsonObject.optString("name")
                val age = jsonObject.optInt("age")
                Log.d("WebViewFragment", "androidMethodName name=$name age=$age")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            "js调用android方法成功"
        }
        fragment?.setListener(object : X5Listener {
            override fun onReceivedIcon(webView: WebView?, icon: Bitmap?) {
                mBinding.ivIcon.setImageBitmap(icon)
            }

            override fun onReceivedTitle(webView: WebView?, title: String?) {
                if (title != null && title.length > 6)
                    mBinding.tvTitle.text = "${title.subSequence(0, 6)}..."
                else
                    mBinding.tvTitle.text = title
            }

            override fun onProgressChanged(webView: WebView?, progress: Int?) {
            }

            override fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?) {
            }

            override fun onPageFinished(webView: WebView?, url: String?) {
            }

            override fun onReceivedError(webView: WebView?) {
            }
        })
    }

    fun pageUp(view: View) {
        fragment?.pageUp()
    }

    fun pageDown(view: View) {
        fragment?.pageDown()
    }

    fun reload(view: View) {
        fragment?.reload()
    }

    fun callJSMethod(view: View) {
        try {
            val params = JSONObject()
            params.put("name", "like1")
            params.put("age", 22)
            fragment?.callJSMethod("jsMethodName", params.toString()) {
                Log.d("TestActivity", "callJsMethod 返回值：$it")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
