package com.like.webview.x5webview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.tencent.smtt.sdk.WebView;

/**
 * 顶部带进度条的WebView
 */
public class X5ProgressBarWebView extends LinearLayout {
    public static final String TAG_WEBVIEW_RECEIVED_ICON = "WebView_onReceivedIcon";
    public static final String TAG_WEBVIEW_RECEIVED_TITLE = "WebView_onReceivedTitle";
    public static final String TAG_WEBVIEW_PAGE_STARTED = "WebView_onPageStarted";
    public static final String TAG_WEBVIEW_PAGE_FINISHED = "WebView_onPageFinished";
    private ProgressBar mProgressBar;
    private X5WebView mWebView;

    public X5ProgressBarWebView(Context context) {
        this(context, null);
    }

    public X5ProgressBarWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WebView getWebView() {
        return mWebView.getWebView();
    }

    public void setErrorView(View view) {
        mWebView.setErrorView(view);
    }

    private void init(Context context, AttributeSet attrs) {
        mProgressBar = new ProgressBar(context, attrs, android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 10));
        mProgressBar.setMax(100);
        addView(mProgressBar);
        mWebView = new X5WebView(context).setX5ProgressBarWebView(this);
        mWebView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        addView(mWebView);
    }

    public void onProgressChanged(int progress) {
        if (mProgressBar == null) {
            return;
        }
        mProgressBar.setProgress(progress);
        if (progress != 100) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public void onDestroy() {
        getWebView().destroy();
    }

}
