package com.like.webview.sample

import android.app.Application
import com.like.common.util.Logger
import com.tencent.smtt.sdk.QbSdk

class WebViewApplication : Application() {
    companion object {
        lateinit var sInstance: Application
    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this
        QbSdk.initX5Environment(this, null)
        Logger.d("WebViewApplication onCreate")
    }
}