package com.naver.httpclientlib;

import java.io.IOException;
import java.util.List;

public final class Response<T> {
    private final okhttp3.Response rawResponse;
    private final Request request;
    private final T body;

    Response(okhttp3.Response rawResponse, Converter<T, ?> converter) throws IOException {
        this.rawResponse = rawResponse;
        this.request = new Request(rawResponse.request());
        body = converter != null ? converter.convertResponseBody(rawResponse.body()) : null;
    }

    public Request request() {
        return request;
    }

    public String header(String name) {
        return rawResponse.header(name);
    }

    public String header(String name, String defaultValue) {
        return rawResponse.header(name, defaultValue);
    }

    public List<String> headers(String name) {
        return rawResponse.headers(name);
    }

    public T body() {
        return body;
    }

    public int code() {
        return rawResponse.code();
    }

    public boolean isSuccessful() {
        return rawResponse.isSuccessful();
    }

    public boolean isRedirect() {
        return rawResponse.isRedirect();
    }

    okhttp3.Response getRawResponse() {
        return rawResponse;
    }

    public void close() {
        rawResponse.close();
    }
}
