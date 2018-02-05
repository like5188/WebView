package com.like.webview.x5webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import static android.webkit.WebSettings.LOAD_DEFAULT;

public class X5WebView extends WebView {
    public static final String TAG_WEBVIEW_RECEIVED_ICON = "WebView_onReceivedIcon";
    public static final String TAG_WEBVIEW_RECEIVED_TITLE = "WebView_onReceivedTitle";
    public static final String TAG_WEBVIEW_PAGE_STARTED = "WebView_onPageStarted";
    public static final String TAG_WEBVIEW_PAGE_FINISHED = "WebView_onPageFinished";
    public Context mContext;

    public X5WebView(Context context) {
        this(context, null);
    }

    public X5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public void init() {
        // 支持获取手势焦点
        requestFocusFromTouch();
        getView().setOnKeyListener((v, keyCode, event) -> {// 此处必须用getView()，因为TBS对WebView进行了封装
            if (keyCode == KeyEvent.KEYCODE_BACK && X5WebView.this.canGoBack()) {
                X5WebView.this.goBack();
                return true;
            }
            return false;
        });
        initWebSettings();// 初始化WebSettings
        setWebViewClient(new X5WebViewClient());
        setWebChromeClient(new X5WebChromeClient((Activity) mContext));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebSettings() {
        WebSettings settings = this.getSettings();
        // 支持JS
        settings.setJavaScriptEnabled(true);
        // 设置WebView是否可以由JavaScript自动打开窗口，默认为false，通常与JavaScript的window.open()配合使用。
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        // 支持插件
        settings.setPluginState(WebSettings.PluginState.ON);
        // 屏幕适配
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        // 支持缩放
        settings.setSupportZoom(false);
        // 显示或不显示缩放按钮（wap网页不支持）。
        settings.setDisplayZoomControls(false);
        // 设置html页面定位的支持
        // 同时也要在清单文件里设置定位的权限支持：
        // <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
        // <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />　　
        settings.setGeolocationEnabled(true);
        // 支持内容重新布局
        // 1.NARROW_COLUMNS：可能的话使所有列的宽度不超过屏幕宽度
        // 2.NORMAL：正常显示不做任何渲染
        // 3.SINGLE_COLUMN：把所有内容放大webview等宽的一列中
        // 用SINGLE_COLUMN类型可以设置页面居中显示，页面可以放大缩小，但这种方法不怎么好，有时候会让你的页面布局走样而且我测了一下，只能显示中间那一块，超出屏幕的部分都不能显示。
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);// 指定WebView的页面布局显示形式，调用该方法会引起页面重绘。默认值为LayoutAlgorithm#NARROW_COLUMNS。
        settings.supportMultipleWindows();// 设置WebView是否支持多窗口。
        settings.setSupportMultipleWindows(true);
        // 设置缓存模式。通常我们可以根据网络情况将这几种模式结合使用，比如有网的时候使用LOAD_DEFAULT，离线时使用LOAD_CACHE_ONLY、LOAD_CACHE_ELSE_NETWORK，让用户不至于在离线时啥都看不到。
        // 缓存模式有四种：
        // LOAD_DEFAULT：默认的缓存使用模式。在进行页面前进或后退的操作时，如果缓存可用并未过期就优先加载缓存，否则从网络上加载数据。这样可以减少页面的网络请求次数。
        // LOAD_CACHE_ELSE_NETWORK：只要缓存可用就加载缓存，哪怕它们已经过期失效。如果缓存不可用就从网络上加载数据。
        // LOAD_NO_CACHE：不加载缓存，只从网络加载数据。
        // LOAD_CACHE_ONLY：不从网络加载数据，只从缓存加载数据。
        settings.setCacheMode(LOAD_DEFAULT);
        settings.setDomStorageEnabled(true);// 启用或禁用DOM缓存。
        settings.setDatabaseEnabled(true);// 启用或禁用数据库缓存。
        settings.setAppCacheEnabled(true);// 启用或禁用应用缓存。
        settings.setAppCachePath(getContext().getCacheDir().getAbsolutePath());// 设置应用缓存路径，这个路径必须是可以让app写入文件的。该方法应该只被调用一次，重复调用会被无视~
        // 设置WebView的UserAgent值。
        settings.setUserAgentString(settings.getUserAgentString() + "");
        // 设置可访问文件
        settings.setAllowFileAccess(true);
        // 当webview调用requestFocus时为webview设置节点。通知WebView是否需要设置一个节点获取焦点当WebView#requestFocus(int,android.graphics.Rect)被调用时，默认为true。
        settings.setNeedInitialFocus(true);
        // 禁止或允许WebView从网络上加载图片。需要注意的是，如果设置是从禁止到允许的转变的话，图片数据并不会在设置改变后立刻去获取，而是在WebView调用reload()的时候才会生效。
        // 这个时候，需要确保这个app拥有访问Internet的权限，否则会抛出安全异常。
        // 通常没有禁止图片加载的需求的时候，完全不用管这个方法，因为当我们的app拥有访问Internet的权限时，这个flag的默认值就是false。
        settings.setBlockNetworkImage(false);
        // 支持自动加载图片
        if (Build.VERSION.SDK_INT >= 19) {
            settings.setLoadsImagesAutomatically(true);
        } else {
            settings.setLoadsImagesAutomatically(false);
        }
        // 设置编码格式
        settings.setDefaultTextEncodingName("UTF-8");
    }

}
