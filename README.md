# WebView

WebView封装类。包含android系统自带的WebView的封装，也包含腾讯x5内核WebView的封装。

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
