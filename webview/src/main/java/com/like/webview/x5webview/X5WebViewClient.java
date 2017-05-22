package com.like.webview.x5webview;

import android.graphics.Bitmap;

import com.like.rxbus.RxBus;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * 帮助WebView处理各种通知和请求事件的
 */
public class X5WebViewClient extends WebViewClient {

    //页面开始加载时
    @Override
    public void onPageStarted(WebView webView, String url, Bitmap favicon) {
        super.onPageStarted(webView, url, favicon);
        // 该方法在WebView开始加载页面且仅在Main frame loading（即整页加载）时回调，一次Main frame的加载只会回调该方法一次。
        // 我们可以在这个方法里设定开启一个加载的动画，告诉用户程序在等待网络的响应。CustomWebChromeClient里面的onProgressChanged()方法来取代。
        RxBus.post(X5WebView.TAG_WEBVIEW_PAGE_STARTED, url);
    }

    //页面完成加载时
    @Override
    public void onPageFinished(WebView webView, String url) {
        super.onPageFinished(webView, url);
        // 该方法只在WebView完成一个页面加载时调用一次（同样也只在Main frame loading时调用），
        // 我们可以可以在此时关闭加载动画，进行其他操作。
        // 注意：由于浏览器内核有可能导致该结束的时候不结束，不该结束的时候提前结束。可以用
        RxBus.post(X5WebView.TAG_WEBVIEW_PAGE_FINISHED, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String s) {
        // 是否在WebView内加载新页面
        // 当我们没有给WebView提供WebViewClient时，WebView如果要加载一个url会向ActivityManager寻求一个适合的处理者来加载该url（比如系统自带的浏览器），
        // 这通常是我们不想看到的。于是我们需要给WebView提供一个WebViewClient，并重写该方法返回true来告知WebView url的加载就在app中进行。
        // 这时便可以实现在app内访问网页。
        webView.loadUrl(s);
        return true;
    }
}
