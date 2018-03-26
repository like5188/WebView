package com.like.webview.x5webview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.like.rxbus.RxBus;
import com.like.webview.R;
import com.tencent.smtt.sdk.WebView;

/**
 * 包含进度条、WebView、errorView(webview_error_view.xml)
 */
public class X5ProgressBarWebView extends LinearLayout {
    private ProgressBar mProgressBar;
    private X5WebView mWebView;
    private boolean isErrorPage;
    private X5Listener mListener;

    public X5ProgressBarWebView(Context context) {
        this(context, null);
    }

    public X5ProgressBarWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public X5ProgressBarWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        init(context, attrs, defStyleAttr);
    }

    public WebView getWebView() {
        return mWebView.getWebView();
    }

    public void addJavascriptInterface(Object javascriptInterface, String name) {
        getWebView().addJavascriptInterface(javascriptInterface, name);
    }

    public void loadUrl(String url) {
        getWebView().loadUrl(url);
    }

    public void addListener(X5Listener listener) {
        mListener = listener;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        RxBus.register(this);
        // 添加进度条
        mProgressBar = new ProgressBar(context, attrs, android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 10));
        mProgressBar.setMax(100);
        addView(mProgressBar);
        // 添加X5WebView
        mWebView = new X5WebView(context, new X5Listener() {
            @Override
            public void onReceivedIcon(WebView webView, Bitmap icon) {
                if (mListener != null) {
                    mListener.onReceivedIcon(webView, icon);
                }
            }

            @Override
            public void onReceivedTitle(WebView webView, String title) {
                if (mListener != null) {
                    mListener.onReceivedTitle(webView, title);
                }
            }

            @Override
            public void onProgressChanged(WebView webView, Integer progress) {
                if (mProgressBar == null) {
                    return;
                }
                mProgressBar.setProgress(progress);
                if (progress != 100) {
                    mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                }
                if (!isNetworkAvailable(getContext())) {
                    showErrorView();
                }
                if (mListener != null) {
                    mListener.onProgressChanged(webView, progress);
                }
            }

            @Override
            public void onPageStarted(WebView webView, String url, Bitmap favicon) {
                isErrorPage = false;
                if (mListener != null) {
                    mListener.onPageStarted(webView, url, favicon);
                }
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                if (!isErrorPage) {
                    mWebView.showWebView();
                }
                if (mListener != null) {
                    mListener.onPageFinished(webView, url);
                }
            }

            @Override
            public void onReceivedError(WebView webView) {
                showErrorView();
                if (mListener != null) {
                    mListener.onReceivedError(webView);
                }
            }
        });
        mWebView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        addView(mWebView);
        // 获取自定义的属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.X5ProgressBarWebView, defStyleAttr, 0);
        int errorViewResId = a.getResourceId(R.styleable.X5ProgressBarWebView_error_view_res_id, -1);
        int progressBarBgColor = a.getColor(R.styleable.X5ProgressBarWebView_progress_bar_bg_color, Color.parseColor("#3F51B5"));
        int progressBarProgressColor = a.getColor(R.styleable.X5ProgressBarWebView_progress_bar_progress_color, Color.parseColor("#FFFFFF"));
        a.recycle();
        // 为X5WebView添加错误页面
        if (errorViewResId != -1) {
            View errorView = View.inflate(context, errorViewResId, null);
            if (errorView != null) {
                mWebView.setErrorView(errorView);
            }
        }
        // 为进度条设置背景颜色
        mProgressBar.setBackgroundColor(progressBarBgColor);
        // 为进度条设置进度条颜色。设置一个ClipDrawable,ClipDrawable是对Drawable进行剪切操作，可以控制这个Drawable的剪切区域，以及相对容器的对齐方式，android中的进度条就是使用一个ClipDrawable实现效果的，它根据level的属性值，决定剪切区域的大小。
        ClipDrawable d = new ClipDrawable(new ColorDrawable(progressBarProgressColor), Gravity.START, ClipDrawable.HORIZONTAL);
        mProgressBar.setProgressDrawable(d);
    }

    private void showErrorView() {
        if (!mWebView.isErrorViewShow()) {
            getWebView().clearHistory();
            mWebView.showErrorView();
            mWebView.getErrorView().setOnClickListener(v -> {
                isErrorPage = false;
                getWebView().reload();
            });
        }
        getWebView().stopLoading();
        isErrorPage = true;
    }

    public void onDestroy() {
        RxBus.unregister(this);
        getWebView().destroy();
    }

    /**
     * 获取网络类型
     *
     * @param context
     * @return
     */
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }
}
