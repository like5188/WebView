package com.like.webview.component

import android.content.Context
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.like.webview.component.service.WebViewService

@Route(path = Consts.SERVICE_WEB_VIEW, name = "登录页面路由服务")
class WebViewServiceImpl : WebViewService {
    private var mContext: Context? = null

    override fun init(context: Context) {
        mContext = context
    }

    override fun getWebViewFragment(url: String): Fragment {
        return ARouter.getInstance().build(Consts.UI_WEB_VIEW_FRAGMENT)
                .withString("url", url)
                .navigation() as WebViewFragment
    }

}
