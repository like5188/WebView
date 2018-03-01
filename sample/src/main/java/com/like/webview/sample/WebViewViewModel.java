package com.like.webview.sample;

import android.graphics.Bitmap;

import com.like.rxbus.RxBus;
import com.like.webview.sample.databinding.ActivityWebviewBinding;
import com.like.webview.x5webview.X5Listener;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by like on 2016/12/9.
 */

public class WebViewViewModel {
    private ActivityWebviewBinding mBinding;

    public WebViewViewModel(ActivityWebviewBinding binding) {
        mBinding = binding;
        mBinding.webView.addListener(new X5Listener() {
            @Override
            public void onReceivedIcon(WebView webView, Bitmap icon) {
                mBinding.ivIcon.setImageBitmap(icon);
            }

            @Override
            public void onReceivedTitle(WebView webView, String title) {
                if (title != null && title.length() > 6)
                    mBinding.tvTitle.setText(title.subSequence(0, 6) + "...");
                else
                    mBinding.tvTitle.setText(title);
            }

            @Override
            public void onProgressChanged(WebView webView, Integer progress) {
                super.onProgressChanged(webView, progress);
            }

            @Override
            public void onPageStarted(WebView webView, String url, Bitmap favicon) {
                super.onPageStarted(webView, url, favicon);
            }

            @Override
            public void onPageFinished(WebView webView, String url) {
                super.onPageFinished(webView, url);
            }

            @Override
            public void onReceivedError(WebView webView) {
                super.onReceivedError(webView);
            }
        });
        RxBus.register(this);
    }

    public WebView getWebView() {
        return mBinding.webView.getWebView();
    }

    public void onDestroy() {
        RxBus.unregister(this);
    }

}
