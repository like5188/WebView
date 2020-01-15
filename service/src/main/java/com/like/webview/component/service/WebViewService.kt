package com.like.webview.component.service

import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * WebView页面服务接口
 */
interface WebViewService : IProvider {
    fun getWebViewFragment(url: String): Fragment
}