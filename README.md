#### 最新版本

模块|WebView
---|---
最新版本|[![Download](https://jitpack.io/v/like5188/WebView.svg)](https://jitpack.io/#like5188/WebView)

## 功能介绍

1、腾讯x5内核WebView的封装，包含进度条。基于版本：'com.tencent.tbs:tbssdk:44153'

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
    implementation 'com.github.like5188:WebView:版本号'
}
```

2、在Application中初始化

```java
    QbSdk.initX5Environment(this,null)
```

3、在xml布局文件中使用
    
    com.like.webview.X5WebView。

    com.like.webview.X5WebViewWithErrorView。带错误视图不带进度条。

    com.like.webview.X5WebViewWithErrorViewAndProgressBar。带错误视图带进度条。

4、也可以直接使用：WebViewFragment、SimpleWebViewActivity。也可以继承 BaseWebViewActivity 封装自己的逻辑。（它们都是对 com.like.webview.X5WebViewWithErrorViewAndProgressBar 的封装）
