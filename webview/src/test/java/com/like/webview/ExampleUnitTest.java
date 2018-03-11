package com.like.webview;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        String a = "http://www.baidu.com/index.js";
        System.out.println(a.substring(a.lastIndexOf("/")));
    }
}