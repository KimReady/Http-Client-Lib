package com.naver.httpclientlib;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class InterceptorChain {
    okhttp3.Interceptor.Chain chain;
    okhttp3.Request.Builder requestBuilder;
    okhttp3.Response rawResponse;

    InterceptorChain(okhttp3.Interceptor.Chain chain) {
        this.chain = chain;
        this.requestBuilder = chain.request().newBuilder();
    }

    public void setRequestUrl(String url) {
        requestBuilder.url(url);
    }

    public void setRequestUrl(URL url) {
        requestBuilder.url(url);
    }

    public void setRequestHeader(String name, String value) {
        requestBuilder.header(name, value);
    }

    public void addRequestHeader(String name, String value) {
        requestBuilder.addHeader(name, value);
    }

    public void removeRequestHeader(String name) {
        requestBuilder.removeHeader(name);
    }

    public String readRequestHeader(String name) {
        return chain.request().header(name);
    }

    public int connectTimeoutMills() {
        return chain.connectTimeoutMillis();
    }

    public void setConnectTimeout(int timeout, TimeUnit unit) {
        chain = chain.withConnectTimeout(timeout, unit);
    }

    public int readTimeoutMills() {
        return chain.readTimeoutMillis();
    }

    public void setReadTimeout(int timeout, TimeUnit unit) {
        chain = chain.withReadTimeout(timeout, unit);
    }

    public int writeTimeoutMills() {
        return chain.writeTimeoutMillis();
    }

    public void setWriteTimeout(int timeout, TimeUnit unit) {
        chain = chain.withWriteTimeout(timeout, unit);
    }

    public Response proceed() throws IOException {
        rawResponse = chain.proceed(requestBuilder.build());
        return new Response<>(rawResponse, null);
    }

}
