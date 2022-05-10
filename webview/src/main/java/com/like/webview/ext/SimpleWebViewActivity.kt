package com.like.webview.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.TypedValue
import com.like.webview.R
import com.tencent.smtt.sdk.WebView

/**
 * 用于简单显示网页，而不进行交互。
 * 如果需要交互，请继承[BaseWebViewActivity]或者[BaseWebViewFragment]。
 */
class SimpleWebViewActivity : BaseWebViewActivity() {
    companion object {
        private const val KEY_URL = "key_url"
        private const val KEY_PROGRESS_BAR_BG_COLOR_RES_ID = "key_progressBarBgColorResId"
        private const val KEY_PROGRESS_BAR_PROGRESS_COLOR_RES_ID = "key_progressBarProgressColorResId"
        private const val KEY_PROGRESS_BAR_HEIGHT = "key_progressBarHeight"

        /**
         * @param url
         * @param progressBarHeight             进度条高度，dp。如果小于等于0，表示无进度条。
         * @param progressBarBgColorResId       进度条背景色
         * @param progressBarProgressColorResId 进度条颜色
         */
        fun start(
            context: Context,
            url: String?,
            progressBarHeight: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, Resources.getSystem().displayMetrics),
            progressBarBgColorResId: Int = R.color.colorPrimary,
            progressBarProgressColorResId: Int = R.color.colorPrimaryDark
        ) {
            Intent(context, SimpleWebViewActivity::class.java).apply {
                if (context !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(KEY_URL, url)
                putExtra(KEY_PROGRESS_BAR_BG_COLOR_RES_ID, progressBarBgColorResId)
                putExtra(KEY_PROGRESS_BAR_PROGRESS_COLOR_RES_ID, progressBarProgressColorResId)
                putExtra(KEY_PROGRESS_BAR_HEIGHT, progressBarHeight)
                context.startActivity(this)
            }
        }
    }

    override fun getWebViewConfig(webView: WebView): WebViewConfig = WebViewConfig().apply {
        url = intent.getStringExtra(KEY_URL)
        progressBarHeight = intent.getFloatExtra(KEY_PROGRESS_BAR_HEIGHT, 0f)
        progressBarBgColorResId = intent.getIntExtra(KEY_PROGRESS_BAR_BG_COLOR_RES_ID, -1)
        progressBarProgressColorResId = intent.getIntExtra(KEY_PROGRESS_BAR_PROGRESS_COLOR_RES_ID, -1)
    }

}
