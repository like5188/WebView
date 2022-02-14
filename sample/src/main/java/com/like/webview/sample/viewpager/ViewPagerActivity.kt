package com.like.webview.sample.viewpager

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.like.webview.sample.R
import com.like.webview.sample.WebViewFragment
import com.like.webview.sample.databinding.ActivityViewPagerBinding

// todo 未解决 viewpager+fragment+webview 内存泄漏问题
class ViewPagerActivity : FragmentActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityViewPagerBinding>(this, R.layout.activity_view_pager)
    }
    private val fragments = listOf(
        WebViewFragment("https://www.baidu.com/"),
        WebViewFragment("https://www.baidu.com/"),
        WebViewFragment("https://www.baidu.com/"),
        WebViewFragment("https://www.baidu.com/")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.vp.adapter = ViewPagerAdapter(fragments, this)
        mBinding.vp.offscreenPageLimit = fragments.size - 1// 避免fragment被销毁导致重新懒加载
    }

    fun showFragment1(view: View) {
        mBinding.vp.setCurrentItem(0, false)
    }

    fun showFragment2(view: View) {
        mBinding.vp.setCurrentItem(1, false)
    }

    fun showFragment3(view: View) {
        mBinding.vp.setCurrentItem(2, false)
    }

    fun showFragment4(view: View) {
        mBinding.vp.setCurrentItem(3, false)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}