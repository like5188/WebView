package com.like.webview.x5webview;

import android.app.Activity;
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
    private ProgressBar mProgressBar;
    private X5WebView mWebView;

    public X5ProgressBarWebView(Context context) {
        this(context, null);
    }

    public X5ProgressBarWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public X5WebView getWebView() {
        return mWebView;
    }

    private void init(Context context, AttributeSet attrs) {
        mProgressBar = new ProgressBar(context, attrs, android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 10));
        mProgressBar.setMax(100);
        addView(mProgressBar);
        mWebView = new X5WebView(context);
        mWebView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        addView(mWebView);

        mWebView.setWebChromeClient(new X5WebChromeClient((Activity) getContext()) {
            @Override
            public void onProgressChanged(WebView webView, int newProgress) {
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
