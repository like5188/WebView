package com.like.webview.androidwebview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.like.logger.Logger;
import com.like.rxbus.RxBus;

import java.util.HashMap;
import java.util.Map;

/**
 * 帮助WebView处理各种通知和请求事件的
 */
public class CustomWebViewClient extends WebViewClient {
    public static String sCurUrl = "";
    private Activity mActivity;
    private Map<String, UrlHandler> mHyBridHandlerMap;

    public CustomWebViewClient(Activity activity) {
        super();
        mActivity = activity;
        mHyBridHandlerMap = new HashMap<>();
    }

    public Map<String, UrlHandler> addHyBridUrlHandler(UrlHandler handler) {
        mHyBridHandlerMap.put(handler.getHandledUrlHost(), handler);
        return mHyBridHandlerMap;
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
        // 该方法在加载页面资源时会回调，每一个资源（比如图片）的加载都会调用一次。
    }

    //页面开始加载时
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        // 该方法在WebView开始加载页面且仅在Main frame loading（即整页加载）时回调，一次Main frame的加载只会回调该方法一次。
        // 我们可以在这个方法里设定开启一个加载的动画，告诉用户程序在等待网络的响应。CustomWebChromeClient里面的onProgressChanged()方法来取代。
        RxBus.post(CustomWebView.TAG_WEBVIEW_PAGE_STARTED, url);
    }


    //页面完成加载时
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        // 该方法只在WebView完成一个页面加载时调用一次（同样也只在Main frame loading时调用），
        // 我们可以可以在此时关闭加载动画，进行其他操作。
        // 注意：由于浏览器内核有可能导致该结束的时候不结束，不该结束的时候提前结束。可以用
        RxBus.post(CustomWebView.TAG_WEBVIEW_PAGE_FINISHED, url);
    }

    //网络错误时回调的方法
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        // 该方法在web页面加载错误时回调，这些错误通常都是由无法与服务器正常连接引起的，最常见的就是网络问题。
        // 在这里写网络错误时的逻辑,比如显示一个错误页面
        // 这个方法有两个地方需要注意：
        // 1.这个方法只在与服务器无法正常连接时调用，类似于服务器返回错误码的那种错误（即HTTP ERROR），该方法是不会回调的，
        // 因为你已经和服务器正常连接上了（全怪官方文档(︶^︶)）；
        // 2.这个方法是新版本的onReceivedError()方法，从API23开始引进，
        // 与旧方法onReceivedError(WebView view,int errorCode,String description,String failingUrl)不同的是，
        // 新方法在页面局部加载发生错误时也会被调用（比如页面里两个子Tab或者一张图片）。
        // 这就意味着该方法的调用频率可能会更加频繁，所以我们应该在该方法里执行尽量少的操作。

//        view.stopLoading();
//        view.loadUrl("file:///android_asset/loadfailed.html");
//        // 延迟清空历史纪录，就能把错误页面清空。
//        view.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                view.clearHistory();
//            }
//        }, 1000);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        // onReceivedError()方法提到onReceivedError并不会在服务器返回错误码时被回调，那么当我们需要捕捉HTTP ERROR并进行相应操作时应该怎么办呢？
        // API23便引入了该方法。当服务器返回一个HTTP ERROR并且它的status code>=400时，该方法便会回调。
        // 这个方法的作用域并不局限于Main Frame，任何资源的加载引发HTTP ERROR都会引起该方法的回调，
        // 所以我们也应该在该方法里执行尽量少的操作，只进行非常必要的错误处理等。
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
        // 当WebView加载某个资源引发SSL错误时会回调该方法，
        // 这时WebView要么执行handler.cancel()取消加载，要么执行handler.proceed()方法继续加载（默认为cancel）。
        // 需要注意的是，这个决定可能会被保留并在将来再次遇到SSL错误时执行同样的操作。
    }

    //是否在WebView内加载新页面
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        // 当我们没有给WebView提供WebViewClient时，WebView如果要加载一个url会向ActivityManager寻求一个适合的处理者来加载该url（比如系统自带的浏览器），
        // 这通常是我们不想看到的。于是我们需要给WebView提供一个WebViewClient，并重写该方法返回true来告知WebView url的加载就在app中进行。
        // 这时便可以实现在app内访问网页。
//        view.loadUrl(request.toString());


        // 对网页中超链接按钮的响应。当按下某个连接时WebViewClient会调用这个方法，并传递参数：按下的url。
        String url = request.toString();
        Logger.w("CustomWebViewClient shouldOverrideUrlLoading url=" + url);
        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();

        if (UrlHandler.HYBRID_SCHEME.equalsIgnoreCase(scheme)) {
            String host = uri.getHost();
            if (!TextUtils.isEmpty(host)) {
                UrlHandler handler = mHyBridHandlerMap.get(host);
                if (handler != null && handler.handleUrl(uri)) {
                    return true;
                } else {

                }
            }
            return super.shouldOverrideUrlLoading(view, url);
        } else if (UrlHandler.HTTP.equalsIgnoreCase(scheme) || UrlHandler.HTTPS.equalsIgnoreCase(scheme)) {
            Logger.d("shouldOverrideUrlLoading = " + url);
            view.loadUrl(url);
            return true;
        } else if (UrlHandler.TEL.equalsIgnoreCase(scheme)) {
            dial(url);
            return true;
        } else if (UrlHandler.SMSTO.equalsIgnoreCase(scheme)) {
            sendToSms(url);
            return true;
        } else if (UrlHandler.MAILTO.equalsIgnoreCase(scheme)) {
            String[] addresses = new String[1];
            addresses[0] = uri.getHost();
            sendToEmail(addresses, null);
            return true;
        } else if (url.equals("file:///android_asset/reload")) {
            Logger.d("webview reload");
            if (!TextUtils.isEmpty(sCurUrl)) {
                view.loadUrl(sCurUrl);
                // 延迟清空历史纪录，就能把错误页面清空。
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.clearHistory();
                    }
                }, 1000);
            }
            return true;
        }
        return true;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return super.shouldInterceptRequest(view, request);
        // 当WebView需要请求某个数据时，这个方法可以拦截该请求来告知app并且允许app本身返回一个数据来替代我们原本要加载的数据。
        // 比如你对web的某个js做了本地缓存，希望在加载该js时不再去请求服务器而是可以直接读取本地缓存的js，这个方法就可以帮助你完成这个需求。
        // 你可以写一些逻辑检测这个request，并返回相应的数据，你返回的数据就会被WebView使用，如果你返回null，WebView会继续向服务器请求。
    }

    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        super.onScaleChanged(view, oldScale, newScale);
        // 当WebView得页面Scale值发生改变时回调。
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        // 默认值为false，重写此方法并return true可以让我们在WebView内处理按键事件。
        return super.shouldOverrideKeyEvent(view, event);
    }


    /**
     * 打电话
     *
     * @param url "tel:13400010001"
     */
    private void dial(String url) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
            mActivity.startActivity(callIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发短信
     *
     * @param url "smsto:13200100001"
     */
    private void sendToSms(String url) {
        try {
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
            mActivity.startActivity(smsIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发邮件
     *
     * @param addresses
     * @param subject
     */
    private void sendToEmail(String[] addresses, String subject) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (emailIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            mActivity.startActivity(emailIntent);
        }
    }

}
