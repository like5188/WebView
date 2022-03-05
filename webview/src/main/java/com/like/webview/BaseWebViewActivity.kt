package com.like.webview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.PixelFormat
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity

/**
 * 对 [WebViewFragment] 的封装
 */
abstract class BaseWebViewActivity : AppCompatActivity() {
    companion object {
        private const val KEY_URL = "key_url"
        private const val KEY_ERROR_VIEW_RES_ID = "key_errorViewResId"
        private const val KEY_PROGRESS_BAR_BG_COLOR_RES_ID = "key_progressBarBgColorResId"
        private const val KEY_PROGRESS_BAR_PROGRESS_COLOR_RES_ID = "key_progressBarProgressColorResId"
        private const val KEY_PROGRESS_BAR_HEIGHT = "key_progressBarHeight"

        /**
         * @param clazz                         [BaseWebViewActivity]的子类
         * @param url
         * @param errorViewResId                错误视图
         * @param progressBarBgColorResId       进度条背景色
         * @param progressBarProgressColorResId 进度条颜色
         * @param progressBarHeight             进度条高度，dp。如果小于等于0，表示无进度条。
         */
        fun start(
            context: Context,
            clazz: Class<*>,
            url: String?,
            errorViewResId: Int = R.layout.webview_error_view,
            progressBarBgColorResId: Int = R.color.colorPrimary,
            progressBarProgressColorResId: Int = R.color.colorPrimaryDark,
            progressBarHeight: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, Resources.getSystem().displayMetrics)
        ) {
            Intent(context, clazz).apply {
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

    var mWebViewFragment: WebViewFragment? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 网页中的视频，上屏幕的时候，可能出现闪烁的情况，需要如下设置：Activity在onCreate时需要设置:
        window.setFormat(PixelFormat.TRANSLUCENT)

        val url = intent.getStringExtra(KEY_URL)
        val errorViewResId = intent.getIntExtra(KEY_ERROR_VIEW_RES_ID, -1)
        val progressBarBgColorResId = intent.getIntExtra(KEY_PROGRESS_BAR_BG_COLOR_RES_ID, -1)
        val progressBarProgressColorResId = intent.getIntExtra(KEY_PROGRESS_BAR_PROGRESS_COLOR_RES_ID, -1)
        val progressBarHeight = intent.getFloatExtra(KEY_PROGRESS_BAR_HEIGHT, 0f)

        if (!url.isNullOrEmpty() && mWebViewFragment == null) {
            supportFragmentManager.beginTransaction().apply {
                WebViewFragment(
                    url,
                    errorViewResId,
                    progressBarBgColorResId,
                    progressBarProgressColorResId,
                    progressBarHeight
                ).apply {
                    add(getFragmentHolderResId(), this)
                    mWebViewFragment = this
                }
            }.commit()
        }
    }

    abstract fun getFragmentHolderResId(): Int

}
