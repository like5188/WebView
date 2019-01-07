#### 最新版本

模块|WebView
---|---
最新版本|[![Download](https://jitpack.io/v/like5188/WebView.svg)](https://jitpack.io/#like5188/WebView)

## 功能介绍

1、WebView封装类。包含android系统自带的WebView的封装，也包含腾讯x5内核WebView的封装。

## 使用方法：

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
        compile 'com.github.like5188:WebView:版本号'
    }
```

2、直接在xml布局文件中使用
com.like.webview.X5WebView。不带进度条。持有com.tencent.smtt.sdk.WebView的引用，可以设置X5Listener监听。
com.like.webview.X5ProgressBarWebView。带进度条。持有com.like.webview.X5WebView的引用，可以设置X5Listener监听。
    进度条的属性可以通过下面四个自定义属性设置：
    app:error_view_res_id="@layout/webview_error_view"
    app:progress_bar_height="3dp"
    app:progress_bar_bg_color="@color/colorPrimary"
    app:progress_bar_progress_color="@color/colorAccent"

3、Proguard
```java
    -dontwarn com.tencent.smtt.**
```
