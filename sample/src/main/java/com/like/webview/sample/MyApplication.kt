package com.like.webview.sample

import android.app.Application
import com.tencent.smtt.sdk.QbSdk

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        QbSdk.initX5Environment(this, null)
    }

}