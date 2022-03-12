package com.like.webview.client

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.like.webview.listener.X5Listener
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView

/**
 * 帮助WebView处理Javascript的对话框，网站图标，网站title，加载进度
 */
internal class X5WebChromeClient(private val mActivity: Activity, private val mListener: X5Listener?) : WebChromeClient() {
    companion object {
        private val TAG = X5WebChromeClient::class.java.simpleName
        private val COVER_SCREEN_PARAMS = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private var mCustomView: View? = null
    private val mDecorView: FrameLayout = mActivity.window.decorView as FrameLayout
    private var mFullscreenContainer: FrameLayout? = null
    private var mCustomViewCallback: IX5WebChromeClient.CustomViewCallback? = null

    override fun onShowFileChooser(p0: WebView?, p1: ValueCallback<Array<Uri>>?, p2: FileChooserParams?): Boolean {
        Log.d(TAG, "onShowFileChooser")
        return mListener?.onShowFileChooser(p0, p1, p2) ?: super.onShowFileChooser(p0, p1, p2)
    }

    override fun onReceivedIcon(webView: WebView?, icon: Bitmap?) {
        super.onReceivedIcon(webView, icon)
        // 用来接收web页面的icon，我们可以在这里将该页面的icon设置到Toolbar。
        mListener?.onReceivedIcon(webView, icon)
        Log.d(TAG, "onReceivedIcon")
    }

    override fun onReceivedTitle(webView: WebView?, title: String?) {
        super.onReceivedTitle(webView, title)
        if (title!!.contains("404") || title.contains("500") || title.contains("Error")) {
            mListener?.onReceivedError(webView)
            mListener?.onReceivedTitle(webView, "")
        } else {
            // 用来接收web页面的title，我们可以在这里将页面的title设置到Toolbar。
            mListener?.onReceivedTitle(webView, title)
        }
        Log.d(TAG, "onReceivedTitle title=$title")
    }

    override fun onProgressChanged(webView: WebView?, i: Int) {
        super.onProgressChanged(webView, i)
        mListener?.onProgressChanged(webView, i)
        Log.d(TAG, "onProgressChanged i=$i")
    }

    /**
     * 全屏播放配置
     */
    override fun onShowCustomView(view: View?, customViewCallback: IX5WebChromeClient.CustomViewCallback?) {
        if (mCustomView != null) {
            customViewCallback!!.onCustomViewHidden()
            return
        }

        mFullscreenContainer = FullscreenHolder(mActivity)
        mFullscreenContainer!!.addView(view, COVER_SCREEN_PARAMS)
        mDecorView.addView(mFullscreenContainer, COVER_SCREEN_PARAMS)
        setStatusBarVisibility(false)
        mCustomView = view
        mCustomViewCallback = customViewCallback
        Log.d(TAG, "onShowCustomView")
    }

    override fun onHideCustomView() {
        if (mCustomView == null) {
            return
        }
        setStatusBarVisibility(true)
        mDecorView.removeView(mFullscreenContainer)

        if (mCustomViewCallback != null) {
            mCustomViewCallback!!.onCustomViewHidden()
            mCustomViewCallback = null
        }
        mFullscreenContainer = null
        mCustomView = null
        Log.d(TAG, "onHideCustomView")
    }

    /**
     * 设置顶部状态栏显示隐藏，并设置横竖屏
     *
     * @param visible
     */
    private fun setStatusBarVisibility(visible: Boolean) {
        val flag = if (visible) 0 else WindowManager.LayoutParams.FLAG_FULLSCREEN
        mActivity.window.setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (visible) {
            mActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            mActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    /**
     * 全屏容器界面
     */
    private class FullscreenHolder(ctx: Context) : FrameLayout(ctx) {

        init {
            setBackgroundColor(ctx.resources.getColor(android.R.color.black))
        }

        override fun onTouchEvent(evt: MotionEvent): Boolean {
            return true
        }
    }

}
