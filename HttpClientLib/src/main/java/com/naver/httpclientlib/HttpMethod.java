package com.naver.httpclientlib;

import java.lang.reflect.Method;

class HttpMethod<ResponseT> {

    static HttpMethod create(HttpClient httpClient, Method method, Object[] args) {
        return new HttpMethod<>(httpClient, method, args);
    }

    private HttpClient httpClient;
    Method method;
    Object[] args;

    HttpMethod(HttpClient httpClient, Method method, Object[] args) {
        this.httpClient = httpClient;
        this.method = method;
        this.args = args;
    }

    public CallTask<ResponseT> invoke() {
        RequestFactory requestFactory = new RequestFactory.Builder(httpClient, method, args).build();
        okhttp3.Call.Factory okhttpCallFactory = httpClient.getCallFactory();

        CallTask<ResponseT> callTask = new RealCallTask<>(this, requestFactory, okhttpCallFactory);

        return callTask;
    }
}
