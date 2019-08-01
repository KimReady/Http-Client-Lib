package com.naver.httpclientlib;

import java.io.IOException;
import java.util.List;

public final class Response<T> {
    private final okhttp3.Response rawResponse;
    private final Converter<T, ?> converter;
    private final Request request;

    Response(okhttp3.Response rawResponse, Converter converter) {
        this.rawResponse = rawResponse;
        this.converter = converter;
        this.request = new Request(rawResponse.request());
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

    public T body() throws IOException {
        if(converter == null) {
            return null;
        }
        return converter.convertResponseBody(rawResponse.body());
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
