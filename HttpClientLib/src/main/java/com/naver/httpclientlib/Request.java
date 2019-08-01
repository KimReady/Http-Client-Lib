package com.naver.httpclientlib;

import java.util.Map;

public class Request {
    final String url;
    final String method;
    final String headers;
    final String contentType;

    public Request(okhttp3.Request request) {
        url = request.url().toString();
        method = request.method();
        headers = request.headers().toString();
        contentType = request.body().contentType().toString();
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public String getHeaders() {
        return headers;
    }
}
