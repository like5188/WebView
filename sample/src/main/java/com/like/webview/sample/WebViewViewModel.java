package com.like.webview.sample;

import android.graphics.Bitmap;
import android.view.View;

import com.like.rxbus.RxBus;
import com.like.rxbus.annotations.RxBusSubscribe;
import com.like.webview.sample.databinding.ActivityWebviewBinding;
import com.like.webview.x5webview.X5WebView;

/**
 * Created by like on 2016/12/9.
 */

public class WebViewViewModel {
    private ActivityWebviewBinding mBinding;

    public WebViewViewModel(ActivityWebviewBinding binding) {
        mBinding = binding;
        RxBus.register(this);
    }

    public void onDestroy() {
        RxBus.unregister(this);
        // 自己手动去调一下才能释放资源。否则就算依赖的 activity 或者 fragment 不在了，资源还是不会被释放干净。。。
        mBinding.webView.destroy();
    }

    // 收到web页面传来的icon
    @RxBusSubscribe(X5WebView.TAG_WEBVIEW_RECEIVED_ICON)
    public void onReceivedIcon(Bitmap icon) {
        mBinding.ivIcon.setImageBitmap(icon);
    }

    // 收到web页面传来的title
    @RxBusSubscribe(X5WebView.TAG_WEBVIEW_RECEIVED_TITLE)
    public void onReceivedTitle(String title) {
        if (title != null && title.length() > 6)
            mBinding.tvTitle.setText(title.subSequence(0, 6) + "...");
        else
            mBinding.tvTitle.setText(title);
    }

    // web页面开始加载
    @RxBusSubscribe(X5WebView.TAG_WEBVIEW_PAGE_STARTED)
    public void onPageStarted(String url) {
        mBinding.progressBar.setVisibility(View.VISIBLE);
    }

    // web页面加载完毕
    @RxBusSubscribe(X5WebView.TAG_WEBVIEW_PAGE_FINISHED)
    public void onPageFinished(String url) {
        mBinding.progressBar.setVisibility(View.GONE);
        // 此处必须设置标题，避免回退时，不能正常显示当前页面的标题
        String title = mBinding.webView.getTitle();
        if (title != null && title.length() > 6)
            mBinding.tvTitle.setText(title.subSequence(0, 6) + "...");
        else
            mBinding.tvTitle.setText(title);
    }

}
