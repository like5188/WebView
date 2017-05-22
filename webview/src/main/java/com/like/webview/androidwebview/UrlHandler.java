package com.like.webview.androidwebview;

import android.net.Uri;

/**
 * Url的处理接口
 */
public interface UrlHandler {
    public static final String HYBRID_SCHEME = "hybrid";
    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    public static final String TEL = "tel";
    public static final String SMSTO = "smsto";
    public static final String MAILTO = "mailto";

    /***
     * 返回Host
     *
     * @return
     */
    String getHandledUrlHost();

    /***
     * 处理URI
     *
     * @param uri
     * @return 已处理返回true, 否则返回false
     */
    boolean handleUrl(Uri uri);

}
