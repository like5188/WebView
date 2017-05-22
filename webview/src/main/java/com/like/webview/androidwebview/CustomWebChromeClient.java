package com.like.webview.androidwebview;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.like.logger.Logger;
import com.like.rxbus.RxBus;

/**
 * 帮助WebView处理Javascript的对话框，网站图标，网站title，加载进度
 */
public class CustomWebChromeClient extends WebChromeClient {
    private Bitmap mDefaultVideoPoster;// 当从video中获取预览图失败时的默认视频预览图
    private WebView mWebView;
    private View mVideoView;
    private CustomViewCallback callback;

    public CustomWebChromeClient() {
    }

    /**
     * @param defaultVideoPoster 当从video中获取预览图失败时的默认视频预览图
     */
    public CustomWebChromeClient(Bitmap defaultVideoPoster) {
        mDefaultVideoPoster = defaultVideoPoster;
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        Logger.d("WebView", "HyBridChromeClient onConsoleMessage=" + consoleMessage.message());
        return super.onConsoleMessage(consoleMessage);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        // 当页面加载的进度发生改变时回调，用来告知主程序当前页面的加载进度。
        super.onProgressChanged(view, newProgress);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view, icon);
        // 用来接收web页面的icon，我们可以在这里将该页面的icon设置到Toolbar。
        RxBus.post(CustomWebView.TAG_WEBVIEW_RECEIVED_ICON, icon);
        mWebView = view;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        // 用来接收web页面的title，我们可以在这里将页面的title设置到Toolbar。
        RxBus.post(CustomWebView.TAG_WEBVIEW_RECEIVED_TITLE, title);
    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        // 当我们的Web页面包含视频时，我们可以在HTML里为它设置一个预览图，WebView会在绘制页面时根据它的宽高为它布局。
        // 而当我们处于弱网状态下时，我们没有比较快的获取该图片，那WebView绘制页面时的gitWidth()方法就会报出空指针异常~ 于是app就crash了。
        // 这时我们就需要重写该方法，在我们尚未获取web页面上的video预览图时，给予它一个本地的图片，避免空指针的发生。
        if (mDefaultVideoPoster != null) {
            return mDefaultVideoPoster;
        }
        return super.getDefaultVideoPoster();
    }

    @Override
    public View getVideoLoadingProgressView() {
        // 重写该方法可以在视频loading时给予一个自定义的View，可以是加载圆环 or something。
        return super.getVideoLoadingProgressView();
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        // 处理Javascript中的Alert对话框。
        return super.onJsAlert(view, url, message, result);
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        // 处理Javascript中的Prompt对话框。
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        // 处理Javascript中的Confirm对话框
        return super.onJsConfirm(view, url, message, result);
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        // 该方法在用户进行了web上某个需要上传文件的操作时回调。
        // 我们应该在这里打开一个文件选择器，如果要取消这个请求我们可以调用filePathCallback.onReceiveValue(null)并返回true。
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
    }

    @Override
    public void onPermissionRequest(PermissionRequest request) {
        super.onPermissionRequest(request);
        // 该方法在web页面请求某个尚未被允许或拒绝的权限时回调，主程序在此时调用grant(String [])或deny()方法。
        // 如果该方法没有被重写，则默认拒绝web页面请求的权限。
    }

    @Override
    public void onPermissionRequestCanceled(PermissionRequest request) {
        super.onPermissionRequestCanceled(request);
        // 该方法在web权限申请权限被取消时回调，这时应该隐藏任何与之相关的UI界面。
    }

    // 以下两个方法是为了支持web页面进入全屏模式而存在的（比如播放视频），如果不实现这两个方法，该web上的内容便不能进入全屏模式。
    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        super.onShowCustomView(view, callback);
        // 该方法在当前页面进入全屏模式时回调，主程序必须提供一个包含当前web内容（视频 or Something）的自定义的View。
        ViewGroup viewGroup = (ViewGroup) mWebView.getParent();
        viewGroup.removeView(mWebView);
        viewGroup.addView(view);
        mVideoView = view;
        this.callback = callback;
    }

    @Override
    public void onHideCustomView() {
        super.onHideCustomView();
        // 该方法在当前页面退出全屏模式时回调，主程序应在这时隐藏之前show出来的View。
        if (callback != null) {
            callback.onCustomViewHidden();
            callback = null;
        }
        if (mVideoView != null) {
            ViewGroup viewGroup = (ViewGroup) mVideoView.getParent();
            viewGroup.removeView(mVideoView);
            viewGroup.addView(mWebView);
        }
    }

}
