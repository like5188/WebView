package com.like.webview.sample

import com.like.base.context.BaseApplication
import com.umeng.analytics.MobclickAgent

class MyApplication : BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        MobclickAgent.openActivityDurationTrack(false)
        MobclickAgent.setSessionContinueMillis(1000)
    }
}
