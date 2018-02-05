package com.like.webview.androidwebview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.like.logger.Logger;

/**
 * 顶部带进度条的WebView
 */
public class ProgressBarWebView extends LinearLayout {
    private ProgressBar mProgressBar;
    private WebView mWebView;

    public ProgressBarWebView(Context context) {
        this(context, null);
    }

    public ProgressBarWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WebView getWebView() {
        return mWebView;
    }

    private void init(Context context, AttributeSet attrs) {
        mProgressBar = new ProgressBar(context, attrs, android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 10));
        mProgressBar.setMax(100);
        addView(mProgressBar);
        mWebView = new CustomWebView(context);
        mWebView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(mWebView);

        mWebView.setWebChromeClient(new CustomWebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Logger.e(newProgress);
                super.onProgressChanged(view, newProgress);
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
