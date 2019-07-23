package com.naver.httpclientlib;

import com.naver.httpclientlib.converter.Converter;

import java.io.IOException;
import java.util.List;

public final class Response<T> {
    private final okhttp3.Response rawResponse;
    private final Converter<T, ?> converter;

    Response(okhttp3.Response rawResponse, Converter converter) {
        this.rawResponse = rawResponse;
        this.converter = converter;
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
