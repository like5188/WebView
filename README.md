#### 最新版本

模块|WebView
---|---
最新版本|[![Download](https://jitpack.io/v/like5188/WebView.svg)](https://jitpack.io/#like5188/WebView)

## 功能介绍

1、腾讯x5内核WebView的封装。基于版本：tbs_sdk_thirdapp_v3.6.0.1371_43624_sharewithdownload_withoutGame_obfs_20181106_121046.jar

2、支持 ARouter 组件化架构。

## 一、非组件化使用方法：

1、引用

在Project的gradle中加入：
```groovy
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
在Module的gradle中加入：
```groovy
    dependencies {
        implementation 'com.github.like5188.WebView:webview:版本号'
    }
```

2、在Application中初始化
```java
    QbSdk.initX5Environment(this, null)
```

3、直接在xml布局文件中使用

    com.like.webview.X5WebView。不带进度条。

    com.like.webview.X5ProgressBarWebView。带进度条。

        进度条的属性可以通过下面四个自定义属性设置：

        app:error_view_res_id="@layout/webview_error_view"

        app:progress_bar_height="3dp"

        app:progress_bar_bg_color="@color/colorPrimary"

        app:progress_bar_progress_color="@color/colorAccent"

4、js 和 android 的相互调用，使用 JavascriptInterface 帮助类。详情见 WebViewFragment

## 二、组件化使用方法：

1、引用

在Project的gradle中加入：
```groovy
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
在Module的gradle中加入：
```groovy
    dependencies {
        implementation 'com.github.like5188:Common:5.3.3'

        implementation 'com.github.like5188.WebView:component:版本号'
        implementation 'com.github.like5188.WebView:webview:版本号'
        implementation 'com.github.like5188.WebView:service:版本号'

        kapt 'com.alibaba:arouter-compiler:1.2.2'
    }
```

2、通过 WebViewService 接口获取 WebViewFragment。然后可以通过 WebViewFragment 进行相关操作，详情见例子。
