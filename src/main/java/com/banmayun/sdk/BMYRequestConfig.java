package com.banmayun.sdk;

import com.banmayun.sdk.http.HttpRequestor;
import com.banmayun.sdk.http.StandardHttpRequestor;

public class BMYRequestConfig {

    public static String defaultLocale = "zh_CN";

    public String clientIdentifier = null;
    public String userLocale = null;
    public HttpRequestor httpRequestor = null;

    public BMYRequestConfig(String clientIdentifier, String userLocale, HttpRequestor httpRequestor) {
        if (clientIdentifier == null) {
            throw new IllegalArgumentException("'clientIdentifier' should not be null");
        }
        if (httpRequestor == null) {
            throw new IllegalArgumentException("'httpRequestor' should not be null");
        }

        this.clientIdentifier = clientIdentifier;
        this.userLocale = userLocale;
        this.httpRequestor = httpRequestor;
    }

    public BMYRequestConfig(String clientIdentifier, String userLocale) {
        this(clientIdentifier, userLocale, StandardHttpRequestor.getInstance());
    }

    public BMYRequestConfig(String clientIdentifier) {
        this(clientIdentifier, defaultLocale);
    }
}
