package com.like.webview.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.like.webview.sample.databinding.ActivityWebviewBinding;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebView;

public class WebViewActivity extends AppCompatActivity {
    private ActivityWebviewBinding mBinding;
    private WebViewViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QbSdk.initX5Environment(this, null);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_webview);
        viewModel = new WebViewViewModel(mBinding);
        getWebView().addJavascriptInterface(new JavascriptObject(), "androidAPI");
        String url = "file:///android_asset/index.html";
        getWebView().loadUrl(url);
    }

    public WebView getWebView() {
        return mBinding.webView.getWebView();
    }

    public void pageUp(View view) {
        getWebView().pageUp(true);
    }

    public void pageDown(View view) {
        getWebView().pageDown(true);
    }

    public void refresh(View view) {
        getWebView().reload();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
        mBinding.webView.onDestroy();
    }
}
