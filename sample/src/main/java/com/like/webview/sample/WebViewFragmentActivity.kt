package com.like.webview.sample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.databinding.DataBindingUtil
import com.like.common.util.Logger
import com.like.webview.BaseWebViewActivity
import com.like.webview.WebViewFragment
import com.like.webview.X5Listener
import com.like.webview.sample.databinding.ActivityWebviewFragmentBinding
import com.tencent.smtt.sdk.CookieManager
import com.tencent.smtt.sdk.WebView
import org.json.JSONObject

class WebViewFragmentActivity : BaseWebViewActivity() {
    companion object {
        private const val KEY_URL = "key_url"
        private const val KEY_ERROR_VIEW_RES_ID = "key_errorViewResId"
        private const val KEY_PROGRESS_BAR_BG_COLOR_RES_ID = "key_progressBarBgColorResId"
        private const val KEY_PROGRESS_BAR_PROGRESS_COLOR_RES_ID = "key_progressBarProgressColorResId"
        private const val KEY_PROGRESS_BAR_HEIGHT = "key_progressBarHeight"

        /**
         * @param url
         * @param errorViewResId                错误视图。如果为 -1，表示无错误视图。
         * @param progressBarHeight             进度条高度，dp。如果小于等于0，表示无进度条。
         * @param progressBarBgColorResId       进度条背景色
         * @param progressBarProgressColorResId 进度条颜色
         */
        fun start(
            context: Context,
            url: String?,
            errorViewResId: Int = R.layout.webview_error_view,
            progressBarHeight: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, Resources.getSystem().displayMetrics),
            progressBarBgColorResId: Int = R.color.colorPrimary,
            progressBarProgressColorResId: Int = R.color.colorPrimaryDark
        ) {
            Intent(context, WebViewFragmentActivity::class.java).apply {
                if (context !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(KEY_URL, url)
                putExtra(KEY_ERROR_VIEW_RES_ID, errorViewResId)
                putExtra(KEY_PROGRESS_BAR_BG_COLOR_RES_ID, progressBarBgColorResId)
                putExtra(KEY_PROGRESS_BAR_PROGRESS_COLOR_RES_ID, progressBarProgressColorResId)
                putExtra(KEY_PROGRESS_BAR_HEIGHT, progressBarHeight)
                context.startActivity(this)
            }
        }

        private class JavascriptInterface {
            @android.webkit.JavascriptInterface// API17及以上的版本中，需要此注解才能调用下面的方法
            fun androidMethod(params: String): String {
                try {
                    val jsonObject = JSONObject(params)
                    val name = jsonObject.optString("name")
                    val age = jsonObject.optInt("age")
                    Log.d("WebViewActivity", "androidMethod name=$name age=$age")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return "js 调用 android 的 androidMethod 方法成功"
            }

            @android.webkit.JavascriptInterface
            fun goBack() {
                Logger.d("js调用了goBack方法")
            }

            @android.webkit.JavascriptInterface
            fun login() {
                Logger.d("js调用了login方法")
            }

            @android.webkit.JavascriptInterface
            fun login(a: String) {
                Logger.d("js调用了login方法，参数：$a")
            }
        }
    }

    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityWebviewFragmentBinding>(this, R.layout.activity_webview_fragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    override fun getFragmentHolderResId(): Int {
        return R.id.fragment_holder
    }

    override fun intWebViewFragment(webViewFragment: WebViewFragment) {
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.removeAllCookie()
        cookieManager.setCookie("http://car1.i.cacf.cn", "mechine_type=android")
        with(webViewFragment) {
            url = intent.getStringExtra(KEY_URL)
            errorViewResId = intent.getIntExtra(KEY_ERROR_VIEW_RES_ID, -1)
            progressBarHeight = intent.getFloatExtra(KEY_PROGRESS_BAR_HEIGHT, 0f)
            progressBarBgColorResId = intent.getIntExtra(KEY_PROGRESS_BAR_BG_COLOR_RES_ID, -1)
            progressBarProgressColorResId = intent.getIntExtra(KEY_PROGRESS_BAR_PROGRESS_COLOR_RES_ID, -1)
            addJavascriptInterface(JavascriptInterface(), "appKcwc")
            setListener(object : X5Listener {
                override fun onReceivedIcon(webView: WebView?, icon: Bitmap?) {
                    mBinding.ivIcon.setImageBitmap(icon)
                }

                override fun onReceivedTitle(webView: WebView?, title: String?) {
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
    }

    fun pageUp(view: View) {
        webViewFragment?.pageUp()
    }

    fun pageDown(view: View) {
        webViewFragment?.pageDown()
    }

    fun reload(view: View) {
        webViewFragment?.reload()
    }

    fun callJSMethod(view: View) {
        webViewFragment?.callJsMethod("jsMethodName") {
            Logger.d("callJsMethod 返回值：$it")
        }
    }

    fun callJSMethodWithParams(view: View) {
        val params = JSONObject()
        params.put("name", "like")
        params.put("age", 1)
        webViewFragment?.callJsMethod(
            "jsMethodNameWithParams",
            params.toString()
        ) {
            Logger.d("callJSMethodWithParams 返回值：$it")
        }
    }

}
