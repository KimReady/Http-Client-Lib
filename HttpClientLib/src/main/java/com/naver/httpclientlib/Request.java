package com.naver.httpclientlib;

public class Request {
    final String url;
    final String method;
    final String headers;
    final String contentType;

    public Request(okhttp3.Request request) {
        url = request.url().toString();
        method = request.method();
        headers = request.headers().toString();
        contentType = (request.body() != null && request.body().contentType() != null) ?
                request.body().contentType().toString()
                : null;
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

    public String getContentType() {
        return contentType;
    }
}
