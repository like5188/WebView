package com.like.webview.androidwebview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

/**
 * 顶部带进度条的WebView
 */
public class ProgressBarWebView extends CustomWebView {
    private ProgressBar mProgressBar;

    public ProgressBarWebView(Context context) {
        this(context, null);
    }

    public ProgressBarWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initProgressBar(context, attrs);
    }

    private void initProgressBar(Context context, AttributeSet attrs) {
        mProgressBar = new ProgressBar(context, attrs, android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 10, 0, 0));
        mProgressBar.setMax(100);
        addView(mProgressBar);
        setWebChromeClient(new CustomWebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
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

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) mProgressBar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        mProgressBar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }
}
