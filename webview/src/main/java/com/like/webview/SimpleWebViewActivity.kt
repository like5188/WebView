package com.like.webview

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.like.webview.databinding.ActivityWebviewBinding

/**
 * 用于简单显示网页，而不进行交互。
 * 如果需要交互，请继承[BaseWebViewActivity]或者直接使用 [WebViewFragment]。
 */
class SimpleWebViewActivity : BaseWebViewActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityWebviewBinding>(
            this,
            R.layout.activity_webview
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
    }

    override fun getFragmentHolderResId(): Int {
        return R.id.fragment_holder
    }

}
