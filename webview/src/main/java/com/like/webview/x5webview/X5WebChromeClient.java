package com.like.webview.x5webview;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.like.rxbus.RxBus;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

/**
 * 帮助WebView处理Javascript的对话框，网站图标，网站title，加载进度
 */
public class X5WebChromeClient extends WebChromeClient {
    private static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    private Activity mActivity;
    private View mCustomView;
    private FrameLayout mDecorView;
    private FrameLayout mFullscreenContainer;
    private IX5WebChromeClient.CustomViewCallback mCustomViewCallback;

    public X5WebChromeClient(Activity activity) {
        mActivity = activity;
        mDecorView = (FrameLayout) activity.getWindow().getDecorView();
    }

    @Override
    public void onReceivedIcon(WebView webView, Bitmap icon) {
        super.onReceivedIcon(webView, icon);
        // 用来接收web页面的icon，我们可以在这里将该页面的icon设置到Toolbar。
        RxBus.post(X5ProgressBarWebView.TAG_WEBVIEW_RECEIVED_ICON, icon);
    }

    @Override
    public void onReceivedTitle(WebView webView, String title) {
        super.onReceivedTitle(webView, title);
        if (title.contains("404") || title.contains("500") || title.contains("Error")) {
            RxBus.postByTag(X5ProgressBarWebView.TAG_WEBVIEW_ON_RECEIVED_ERROR);
            RxBus.post(X5ProgressBarWebView.TAG_WEBVIEW_RECEIVED_TITLE, "");
            return;
        }
        // 用来接收web页面的title，我们可以在这里将页面的title设置到Toolbar。
        RxBus.post(X5ProgressBarWebView.TAG_WEBVIEW_RECEIVED_TITLE, title);
    }

    @Override
    public void onProgressChanged(WebView webView, int i) {
        super.onProgressChanged(webView, i);
        RxBus.post(X5ProgressBarWebView.TAG_WEBVIEW_ON_PROGRESS_CHANGED, i);
    }

    /**
     * 全屏播放配置
     */
    @Override
    public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
        if (mCustomView != null) {
            customViewCallback.onCustomViewHidden();
            return;
        }

        mFullscreenContainer = new FullscreenHolder(mActivity);
        mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
        mDecorView.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
        setStatusBarVisibility(false);
        mCustomView = view;
        mCustomViewCallback = customViewCallback;

    }

    @Override
    public void onHideCustomView() {
        if (mCustomView == null) {
            return;
        }
        setStatusBarVisibility(true);
        mDecorView.removeView(mFullscreenContainer);

        if (mCustomViewCallback != null) {
            mCustomViewCallback.onCustomViewHidden();
            mCustomViewCallback = null;
        }
        mFullscreenContainer = null;
        mCustomView = null;
    }

    /**
     * 设置顶部状态栏显示隐藏，并设置横竖屏
     *
     * @param visible
     */
    private void setStatusBarVisibility(boolean visible) {
        int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
        mActivity.getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (visible) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    /**
     * 全屏容器界面
     */
    private static class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }

}
