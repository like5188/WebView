package com.like.webview.x5webview;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.like.logger.Logger;
import com.like.rxbus.RxBus;
import com.like.rxbus.annotations.RxBusSubscribe;
import com.like.webview.R;
import com.tencent.smtt.sdk.WebView;

/**
 * 包含进度条、WebView、errorView(webview_error_view.xml)
 */
public class X5ProgressBarWebView extends LinearLayout {
    public static final String TAG_WEBVIEW_RECEIVED_ICON = "WebView_onReceivedIcon";
    public static final String TAG_WEBVIEW_RECEIVED_TITLE = "WebView_onReceivedTitle";
    public static final String TAG_WEBVIEW_PAGE_STARTED = "WebView_onPageStarted";
    public static final String TAG_WEBVIEW_PAGE_FINISHED = "WebView_onPageFinished";
    public static final String TAG_WEBVIEW_ON_RECEIVED_ERROR = "WebView_onReceivedError";
    public static final String TAG_WEBVIEW_ON_PROGRESS_CHANGED = "WebView_onProgressChanged";
    private ProgressBar mProgressBar;
    private X5WebView mWebView;
    private boolean isErrorPage;

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

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        RxBus.register(this);
        // 添加进度条
        mProgressBar = new ProgressBar(context, attrs, android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 10));
        mProgressBar.setMax(100);
        addView(mProgressBar);
        // 添加X5WebView
        mWebView = new X5WebView(context);
        mWebView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        addView(mWebView);
        // 为X5WebView添加错误页面
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.X5ProgressBarWebView, defStyleAttr, 0);
        int errorViewResId = a.getResourceId(R.styleable.X5ProgressBarWebView_error_view_res_id, -1);
        a.recycle();
        if (errorViewResId != -1) {
            View errorView = View.inflate(context, errorViewResId, null);
            if (errorView != null) {
                mWebView.setErrorView(errorView);
            }
        }
    }

    @RxBusSubscribe(TAG_WEBVIEW_ON_PROGRESS_CHANGED)
    public void TAG_WEBVIEW_ON_PROGRESS_CHANGED(int progress) {
        Logger.i("TAG_WEBVIEW_ON_PROGRESS_CHANGED");
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
    }

    @RxBusSubscribe(TAG_WEBVIEW_ON_RECEIVED_ERROR)
    public void TAG_WEBVIEW_ON_RECEIVED_ERROR() {
        Logger.d("TAG_WEBVIEW_ON_RECEIVED_ERROR");
        showErrorView();
    }

    @RxBusSubscribe(TAG_WEBVIEW_PAGE_FINISHED)
    public void TAG_WEBVIEW_PAGE_FINISHED(String url) {
        Logger.v("TAG_WEBVIEW_PAGE_FINISHED");
        if (!isErrorPage) {
            mWebView.showWebView();
        }
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
