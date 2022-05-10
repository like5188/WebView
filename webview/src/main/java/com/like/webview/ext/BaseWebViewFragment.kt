package com.like.webview.ext

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tencent.smtt.sdk.WebView

/*
生命周期：
// 打开包含Fragment的Activity
E/FragmentContainer: onCreate
E/FragmentContainer: onStart
E/FragmentContainer: onResume
W/Fragment1: onAttach
W/Fragment1: onCreate
W/Fragment1: onCreateView
W/Fragment1: onViewCreated
W/Fragment1: onActivityCreated
W/Fragment1: onViewStateRestored
W/Fragment1: onStart
W/Fragment1: onLazyLoadData
W/Fragment1: onResume

// 转到后台运行
W/Fragment1: onPause
E/FragmentContainer: onPause
W/Fragment1: onStop
E/FragmentContainer: onStop
W/Fragment1: onSaveInstanceState
E/FragmentContainer: onSaveInstanceState

// 转到前台运行
E/FragmentContainer: onRestart
W/Fragment1: onStart
E/FragmentContainer: onStart
E/FragmentContainer: onResume
W/Fragment1: onResume

// 关闭Activity
W/Fragment1: onPause
E/FragmentContainer: onPause
W/Fragment1: onStop
E/FragmentContainer: onStop
W/Fragment1: onDestroyView
W/Fragment1: onDestroy
W/Fragment1: onDetach
E/FragmentContainer: onDestroy

// 旋转屏幕
W/Fragment1: onPause
E/FragmentContainer: onPause
W/Fragment1: onStop
E/FragmentContainer: onStop
W/Fragment1: onSaveInstanceState
E/FragmentContainer: onSaveInstanceState
W/Fragment1: onDestroyView
W/Fragment1: onDestroy
W/Fragment1: onDetach
E/FragmentContainer: onDestroy
W/Fragment1: onAttach
W/Fragment1: onCreate
E/FragmentContainer: onCreate
W/Fragment1: onCreateView
W/Fragment1: onViewCreated
W/Fragment1: onActivityCreated
W/Fragment1: onViewStateRestored
W/Fragment1: onStart
E/FragmentContainer: onStart
E/FragmentContainer: onRestoreInstanceState
E/FragmentContainer: onResume
W/Fragment1: onLazyLoadData
W/Fragment1: onResume
 */

/**
 * 注意：
 * 1、Fragment 必须包含公共的无参构造函数，否则在重建时会报错：could not find Fragment constructor。
 * 因为私有的构造函数无法通过该Constructor.newInstance方法调用。详情见Fragment的instantiate方法。
 * 所以这里采用 abstract 方法的方式获取数据，这样也能控制生命周期。
 * 2、不能使用内部类创建 Fragment，否则会报错：Fragment null must be a public static class to be properly recreated from instance state.
 * 只能继承此类创建 Fragment。
 */
abstract class BaseWebViewFragment : Fragment() {
    private val webViewHelper: WebViewHelper by lazy {
        WebViewHelper()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        webViewHelper.onCreate(context) {
            getWebViewConfig(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return webViewHelper.x5WebViewWithProgressBar
    }

    override fun onPause() {
        super.onPause()
        webViewHelper.onPause()
    }

    override fun onResume() {
        super.onResume()
        webViewHelper.onResume()
    }

    override fun onDestroyView() {
        webViewHelper.onDestroy()
        super.onDestroyView()
    }

    abstract fun getWebViewConfig(webView: WebView): WebViewConfig

}
