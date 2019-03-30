package com.like.webview

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log

import com.tencent.smtt.export.external.interfaces.WebResourceError
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

/**
 * 帮助WebView处理各种通知和请求事件的
 */
class X5WebViewClient(private val mListener: X5Listener?) : WebViewClient() {
    companion object {
        private val TAG = X5WebViewClient::class.java.simpleName
    }

    //页面开始加载时
    override fun onPageStarted(webView: WebView?, url: String, favicon: Bitmap?) {
        super.onPageStarted(webView, url, favicon)
        // 该方法在WebView开始加载页面且仅在Main frame loading（即整页加载）时回调，一次Main frame的加载只会回调该方法一次。
        // 我们可以在这个方法里设定开启一个加载的动画，告诉用户程序在等待网络的响应。CustomWebChromeClient里面的onProgressChanged()方法来取代。
        mListener?.onPageStarted(webView, url, favicon)
        Log.v(TAG, "onPageStarted url=$url")
    }

    //页面完成加载时
    override fun onPageFinished(webView: WebView?, url: String?) {
        super.onPageFinished(webView, url)
        // 该方法只在WebView完成一个页面加载时调用一次（同样也只在Main frame loading时调用），
        // 我们可以可以在此时关闭加载动画，进行其他操作。
        // 注意：由于浏览器内核有可能导致该结束的时候不结束，不该结束的时候提前结束。可以用
        mListener?.onPageFinished(webView, url)
        Log.v(TAG, "onPageFinished url=$url")
    }

    //网络错误时回调的方法
    override fun onReceivedError(webView: WebView?, webResourceRequest: WebResourceRequest, webResourceError: WebResourceError) {
        super.onReceivedError(webView, webResourceRequest, webResourceError)
        // 该方法在web页面加载错误时回调，这些错误通常都是由无法与服务器正常连接引起的，最常见的就是网络问题。但是捕获不到404、500等错误
        // 在这里写网络错误时的逻辑,比如显示一个错误页面
        // 这个方法有两个地方需要注意：
        // 1.这个方法只在与服务器无法正常连接时调用，类似于服务器返回错误码的那种错误（即HTTP ERROR），该方法是不会回调的，
        // 因为你已经和服务器正常连接上了（全怪官方文档(︶^︶)）；
        // 2.这个方法是新版本的onReceivedError()方法，从API23开始引进，
        // 与旧方法onReceivedError(WebView view,int errorCode,String description,String failingUrl)不同的是，
        // 新方法在页面局部加载发生错误时也会被调用（比如页面里两个子Tab或者一张图片）。
        // 这就意味着该方法的调用频率可能会更加频繁，所以我们应该在该方法里执行尽量少的操作。

        // 在Android6.0以上的机器上，网页中的任意一个资源获取不到（比如字体），网页就很可能显示自定义的错误界面。
        // 尤其是如果Html用了本地化技术，’ERR_FILE_NOT_FOUND’开始变得特别常见。
        // 为了避免这样的错误。获取当前的网络请求是否是为main frame创建的。
        if (webResourceRequest.isForMainFrame) {
            mListener?.onReceivedError(webView)
        }
        Log.v(TAG, "onReceivedError url=${webResourceRequest.url}")
    }

    override fun shouldOverrideUrlLoading(webView: WebView?, url: String?): Boolean {
        // 是否在WebView内加载新页面
        // 当我们没有给WebView提供WebViewClient时，WebView如果要加载一个url会向ActivityManager寻求一个适合的处理者来加载该url（比如系统自带的浏览器），
        // 这通常是我们不想看到的。于是我们需要给WebView提供一个WebViewClient，并重写该方法返回true来告知WebView url的加载就在app中进行。
        // 这时便可以实现在app内访问网页。

        // 如下方案可在非微信内部WebView的H5页面中调出微信支付
        if (url != null && url.startsWith("weixin://wap/pay?")) {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(url)
            webView?.context?.startActivity(intent)
            return true
        }

        webView?.loadUrl(url)
        Log.v(TAG, "shouldOverrideUrlLoading url=$url")

        // 1.返回true，即根据代码逻辑执行相应操作，webview不加载该url；
        // 2.返回false，除执行相应代码外，webview加载该url；
        return true
    }

}
