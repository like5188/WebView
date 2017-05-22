package com.like.webview.x5webview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

/**
 * 顶部带进度条的WebView
 */
public class X5ProgressBarWebView extends X5WebView {
    private ProgressBar mProgressBar;

    public X5ProgressBarWebView(Context context) {
        this(context, null);
    }

    public X5ProgressBarWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initProgressBar(context, attrs);
    }

    private void initProgressBar(Context context, AttributeSet attrs) {
        mProgressBar = new ProgressBar(context, attrs, android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 10));
        mProgressBar.setMax(100);
        addView(mProgressBar);
        setWebChromeClient(new X5WebChromeClient((Activity) context) {
            @Override
            public void onProgressChanged(com.tencent.smtt.sdk.WebView webView, int newProgress) {
                super.onProgressChanged(webView, newProgress);
                if (mProgressBar == null) {
                    return;
                }
                mProgressBar.setProgress(newProgress);
                if (newProgress != 100) {
                    mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

}
