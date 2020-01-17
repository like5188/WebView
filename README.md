#### 最新版本

模块|WebView
---|---
最新版本|[![Download](https://jitpack.io/v/like5188/WebView.svg)](https://jitpack.io/#like5188/WebView)

## 功能介绍

1、腾讯x5内核WebView的封装，包含进度条。基于版本：tbs_sdk_thirdapp_v4.3.0.1148_43697_sharewithdownloadwithfile_withoutGame_obfs_20190805_175505.jar

## 使用方法：

1、引用

在Project的gradle中加入：
```groovy
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
在Module的gradle中加入：
```groovy
    dependencies {
        implementation 'com.github.like5188:WebView:版本号'
    }
```

2、在Application中初始化
```java
    QbSdk.initX5Environment(this, null)
```

3、直接在xml布局文件中使用

    com.like.webview.X5WebView。不带进度条。

    com.like.webview.X5ProgressBarWebView。带进度条。

        进度条的属性可以通过下面四个自定义属性设置：

        app:error_view_res_id="@layout/webview_error_view"

        app:progress_bar_height="3dp"

        app:progress_bar_bg_color="@color/colorPrimary"

        app:progress_bar_progress_color="@color/colorAccent"

4、js 和 android 的相互调用，使用 JavascriptInterface 帮助类
```java
    初始化webView
    private val mX5ProgressBarWebView: X5ProgressBarWebView by lazy {
        mBinding.webView
    }
    private val mWebView: WebView by lazy {
        mX5ProgressBarWebView.getWebView()
    }
    private val mJavascriptInterface by lazy { JavascriptInterface() }

    x5ProgressBarWebView.getWebView().addJavascriptInterface(mJavascriptInterface, "androidAPI")

    // js调用android
    mJavascriptInterface.registerAndroidMethodForJSCall("androidMethodName") {
        try {
           val jsonObject = JSONObject(it)
           val name = jsonObject.optString("name")
           val age = jsonObject.optInt("age")
           Log.d("WebViewActivity", "androidMethodName name=$name age=$age")
        } catch (e: Exception) {
           e.printStackTrace()
        }
        "js调用android方法成功"
    }

    // android调用js
    try {
        val params = JSONObject()
        params.put("name", "like1")
        params.put("age", 22)
        mJavascriptInterface.callJsMethod(mWebView, "jsMethodName", params.toString()) {
            Log.d("WebViewActivity", "callJsMethod 返回值：$it")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    // 释放资源
    override fun onDestroy() {
        super.onDestroy()
        mWebView.destroy()
        mJavascriptInterface.clearAndroidMethodForJSCall()
    }
```