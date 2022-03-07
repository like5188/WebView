package com.like.webview.sample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.databinding.DataBindingUtil
import com.like.webview.BaseWebViewActivity
import com.like.webview.WebViewFragmentConfig
import com.like.webview.X5ListenerAdapter
import com.like.webview.sample.databinding.ActivityWebviewFragmentBinding
import com.tencent.smtt.sdk.WebView

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

    override fun getWebViewFragmentConfig(): WebViewFragmentConfig = WebViewFragmentConfig().apply {
        url = intent.getStringExtra(KEY_URL)
        errorViewResId = intent.getIntExtra(KEY_ERROR_VIEW_RES_ID, -1)
        progressBarHeight = intent.getFloatExtra(KEY_PROGRESS_BAR_HEIGHT, 0f)
        progressBarBgColorResId = intent.getIntExtra(KEY_PROGRESS_BAR_BG_COLOR_RES_ID, -1)
        progressBarProgressColorResId = intent.getIntExtra(KEY_PROGRESS_BAR_PROGRESS_COLOR_RES_ID, -1)
        javascriptInterfaceMap["appKcwc"] = JsUtils.JavascriptInterface()
        x5Listener = object : X5ListenerAdapter() {
            override fun onReceivedIcon(webView: WebView?, icon: Bitmap?) {
                mBinding.ivIcon.setImageBitmap(icon)
            }

            override fun onReceivedTitle(webView: WebView?, title: String?) {
                mBinding.tvTitle.text = title
            }
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
        JsUtils.callJSMethod(webViewFragment)
    }

    fun callJSMethodWithParams(view: View) {
        JsUtils.callJSMethodWithParams(webViewFragment)
    }

}
