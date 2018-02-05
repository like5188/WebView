package com.like.webview.sample;

import android.webkit.JavascriptInterface;

import com.like.webview.x5webview.X5WebView;

/**
 * 用于与web页面进行交互的实例代码
 */
public class JavascriptObject {
    private X5WebView mWebView;

    public JavascriptObject(X5WebView webView) {
        mWebView = webView;
    }

    // js调用android方法
    @JavascriptInterface // API17及以上的版本中，需要此注解才能调用下面的方法
    public void gotoOrderConfirm(String goods_list, int activity_type) {
    }

    // android调用js方法
    @JavascriptInterface
    public void updateImage(final int position, final String imagePath) {
        mWebView.post(() -> mWebView.loadUrl("javascript:serverid(" + position + ",'" + imagePath + "')"));
    }
}
