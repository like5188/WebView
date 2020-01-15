package com.like.webview.component

import android.app.Application
import android.content.Context
import android.util.Log
import com.like.common.base.IModuleApplication
import com.tencent.smtt.sdk.QbSdk

class MyApplication : IModuleApplication {
    override fun attachBaseContext(base: Context?) {
    }

    override fun onCreate(application: Application) {
        QbSdk.initX5Environment(application, null)
        Log.d("MyApplication", "webview component onCreate")
    }

    override fun onTerminate(application: Application) {
    }

}