# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#TBS浏览器服务
-dontwarn com.tencent.smtt.sdk.**
#WebView中，解决混淆后导致的问题
-keepclassmembers class * extends android.webkit.WebChromeClient {
   public void openFileChooser(...);
}
-keep class android.webkit.JavascriptInterface {*;}
-keep public class com.like.commonlib.JavascriptObject{
    public void gotoOrderConfirm(...);
}