package com.like.webview.sample;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.like.webview.sample.databinding.ActivityWebviewBinding;
import com.tencent.smtt.sdk.QbSdk;

public class WebViewActivity extends AppCompatActivity {
    private ActivityWebviewBinding mBinding;
    private WebViewViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QbSdk.initX5Environment(this, null);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_webview);
        viewModel = new WebViewViewModel(mBinding);
        String url = "https://www.baidu.com";
        mBinding.webView.loadUrl(url);

    }

    public void pageUp(View view) {
        mBinding.webView.pageUp(true);
    }

    public void pageDown(View view) {
        mBinding.webView.pageDown(true);
    }

    public void refresh(View view) {
        mBinding.webView.reload();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }
}
