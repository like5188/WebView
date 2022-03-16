package com.like.webview.core

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.export.external.interfaces.WebResourceError
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.sdk.*

/**
 * 设置好的[WebView]
 */
open class X5WebView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    WebView(context, attrs, defStyleAttr) {
    open var x5Listener: X5Listener? = null

    init {
        // 支持获取手势焦点
        requestFocusFromTouch()
        // 首次初始化冷启动优化，在调用TBS初始化、创建WebView之前进行如下配置
        QbSdk.initTbsSettings(
            mapOf(
                TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER to true,
                TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE to true
            )
        )
        // 此处必须用getView()，因为TBS对WebView进行了封装
        view?.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && canGoBack()) {
                goBack()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        val listener = object : X5Listener {
            override fun onShowFileChooser(
                webView: WebView?,
                callback: ValueCallback<Array<Uri>>?,
                params: WebChromeClient.FileChooserParams?
            ): Boolean {
                return x5Listener?.onShowFileChooser(webView, callback, params) ?: false
            }

            override fun onReceivedIcon(webView: WebView?, icon: Bitmap?) {
                x5Listener?.onReceivedIcon(webView, icon)
            }

            override fun onReceivedTitle(webView: WebView?, title: String?) {
                x5Listener?.onReceivedTitle(webView, title)
            }

            override fun onProgressChanged(webView: WebView?, progress: Int?) {
                x5Listener?.onProgressChanged(webView, progress)
            }

            override fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?) {
                x5Listener?.onPageStarted(webView, url, favicon)
            }

            override fun onPageFinished(webView: WebView?, url: String?) {
                x5Listener?.onPageFinished(webView, url)
            }

            override fun onReceivedError(webView: WebView?, webResourceRequest: WebResourceRequest, webResourceError: WebResourceError) {
                x5Listener?.onReceivedError(webView, webResourceRequest, webResourceError)
            }

            override fun onShowCustomView(view: View?, callback: IX5WebChromeClient.CustomViewCallback?) {
                x5Listener?.onShowCustomView(view, callback)
            }

            override fun onHideCustomView() {
                x5Listener?.onHideCustomView()
            }
        }
        webViewClient = X5WebViewClient(listener)
        webChromeClient = X5WebChromeClient(listener)
        with(settings) {
            // 支持JS
            javaScriptEnabled = true
            // 设置WebView是否可以由JavaScript自动打开窗口，默认为false，通常与JavaScript的window.open()配合使用。
            javaScriptCanOpenWindowsAutomatically = false
            // 支持插件
            pluginState = WebSettings.PluginState.ON
            // 屏幕适配
            useWideViewPort = true// 将图片调整到适合webview的大小
            loadWithOverviewMode = true// 缩放至屏幕的大小
            // 缩放操作
            setSupportZoom(true)// 支持缩放，默认为true。是下面那个的前提
            builtInZoomControls = true// 设置内置的缩放控件。若为false，则该WebView不可缩放
            displayZoomControls = false// 隐藏原生的缩放控件（wap网页不支持）
            // 设置html页面定位的支持
            // 同时也要在清单文件里设置定位的权限支持：
            // <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
            // <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />　　
            setGeolocationEnabled(true)
            // 支持内容重新布局
            // 1.NARROW_COLUMNS：可能的话使所有列的宽度不超过屏幕宽度
            // 2.NORMAL：正常显示不做任何渲染
            // 3.SINGLE_COLUMN：把所有内容放大webview等宽的一列中
            // 用SINGLE_COLUMN类型可以设置页面居中显示，页面可以放大缩小，但这种方法不怎么好，有时候会让你的页面布局走样而且我测了一下，只能显示中间那一块，超出屏幕的部分都不能显示。
            layoutAlgorithm =
                WebSettings.LayoutAlgorithm.SINGLE_COLUMN// 指定WebView的页面布局显示形式，调用该方法会引起页面重绘。默认值为LayoutAlgorithm#NARROW_COLUMNS。
            supportMultipleWindows()// 设置WebView是否支持多窗口。
            setSupportMultipleWindows(true)
            // 设置缓存模式。通常我们可以根据网络情况将这几种模式结合使用，比如有网的时候使用LOAD_DEFAULT，离线时使用LOAD_CACHE_ONLY、LOAD_CACHE_ELSE_NETWORK，让用户不至于在离线时啥都看不到。
            // 缓存模式有四种：
            // LOAD_DEFAULT：默认的缓存使用模式。在进行页面前进或后退的操作时，如果缓存可用并未过期就优先加载缓存，否则从网络上加载数据。这样可以减少页面的网络请求次数。
            // LOAD_CACHE_ELSE_NETWORK：只要缓存可用就加载缓存，哪怕它们已经过期失效。如果缓存不可用就从网络上加载数据。
            // LOAD_NO_CACHE：不加载缓存，只从网络加载数据。微信H5支付时需要设置。
            // LOAD_CACHE_ONLY：不从网络加载数据，只从缓存加载数据。
            cacheMode = WebSettings.LOAD_DEFAULT
            domStorageEnabled = true// 启用或禁用DOM缓存。
            databaseEnabled = true// 启用或禁用数据库缓存。
            setAppCacheEnabled(true)// 启用或禁用应用缓存。
            setAppCachePath(context.cacheDir.absolutePath)// 设置应用缓存路径，这个路径必须是可以让app写入文件的。该方法应该只被调用一次，重复调用会被无视~
            // 设置WebView的UserAgent值。
            userAgentString = userAgentString
            // 设置可访问文件
            allowFileAccess = true
            // 当webview调用requestFocus时为webview设置节点。通知WebView是否需要设置一个节点获取焦点当WebView#requestFocus(int,android.graphics.Rect)被调用时，默认为true。
            setNeedInitialFocus(true)
            // 禁止或允许WebView从网络上加载图片。需要注意的是，如果设置是从禁止到允许的转变的话，图片数据并不会在设置改变后立刻去获取，而是在WebView调用reload()的时候才会生效。
            // 这个时候，需要确保这个app拥有访问Internet的权限，否则会抛出安全异常。
            // 通常没有禁止图片加载的需求的时候，完全不用管这个方法，因为当我们的app拥有访问Internet的权限时，这个flag的默认值就是false。
            blockNetworkImage = false
            // 支持自动加载图片
            loadsImagesAutomatically = true
            // 设置编码格式
            defaultTextEncodingName = "UTF-8"
            setAppCacheMaxSize(1024 * 1024 * 8)
        }
    }

    /**
     * 需要放在super.onDestroy();之前调用，防止内存泄漏。
     */
    override fun destroy() {
        x5Listener = null
        try {
            loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            parent?.let {
                (it as ViewGroup).removeView(this)
            }
            stopLoading()
            settings?.javaScriptEnabled = false
            clearFormData()
            clearSslPreferences()
            clearHistory()
            clearCache(true)
            removeAllViewsInLayout()
            removeAllViews()
            webViewClient = null
            webChromeClient = null
            CookieSyncManager.getInstance().stopSync()
            super.destroy()
            Log.d("X5WebView", "destroy")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
