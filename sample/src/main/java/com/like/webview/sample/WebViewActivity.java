package com.like.webview.sample;

import android.databinding.DataBindingUtil;
import android.view.View;

import com.like.base.context.BaseActivity;
import com.like.base.viewmodel.BaseViewModel;
import com.like.webview.sample.databinding.ActivityWebviewBinding;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

public class WebViewActivity extends BaseActivity {
    private ActivityWebviewBinding mBinding;
    private WebViewViewModel viewModel;

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

    @Override
    protected BaseViewModel getViewModel() {
        QbSdk.initX5Environment(this, null);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_webview);
        viewModel = new WebViewViewModel(mBinding);
        getWebView().addJavascriptInterface(new JavascriptObject(), "androidAPI");
//        String url = "file:///android_asset/index.html";

        String url = "http://wxdemo-gj.ynrjkj.com:8181";
        getWebView().getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 支持微信H5支付

        getWebView().loadUrl(url);
        return null;
    }

}
