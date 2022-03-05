package com.like.webview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.databinding.DataBindingUtil
import com.like.webview.databinding.ActivityWebviewBinding

/**
 * 用于简单显示网页，而不进行交互。
 * 如果需要交互，请继承[BaseWebViewActivity]或者直接使用 [WebViewFragment]。
 */
class SimpleWebViewActivity : BaseWebViewActivity() {
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
            Log.w(
                "Logger",
                "url=$url errorViewResId=$errorViewResId progressBarHeight=$progressBarHeight progressBarBgColorResId=$progressBarBgColorResId progressBarProgressColorResId=$progressBarProgressColorResId"
            )
            Intent(context, SimpleWebViewActivity::class.java).apply {
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
        DataBindingUtil.setContentView<ActivityWebviewBinding>(
            this,
            R.layout.activity_webview
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        val url = intent.getStringExtra(KEY_URL)
        val errorViewResId = intent.getIntExtra(KEY_ERROR_VIEW_RES_ID, -1)
        val progressBarHeight = intent.getFloatExtra(KEY_PROGRESS_BAR_HEIGHT, 0f)
        val progressBarBgColorResId = intent.getIntExtra(KEY_PROGRESS_BAR_BG_COLOR_RES_ID, -1)
        val progressBarProgressColorResId = intent.getIntExtra(KEY_PROGRESS_BAR_PROGRESS_COLOR_RES_ID, -1)
        Log.e(
            "Logger",
            "url=$url errorViewResId=$errorViewResId progressBarHeight=$progressBarHeight progressBarBgColorResId=$progressBarBgColorResId progressBarProgressColorResId=$progressBarProgressColorResId"
        )
        mWebViewFragment?.init(url, errorViewResId, progressBarHeight, progressBarBgColorResId, progressBarProgressColorResId)
    }

    override fun getFragmentHolderResId(): Int {
        return R.id.fragment_holder
    }

}
