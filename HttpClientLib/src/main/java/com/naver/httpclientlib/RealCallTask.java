package com.naver.httpclientlib;

import java.io.IOException;
import java.lang.reflect.Method;

class RealCallTask<T> implements CallTask<T> {
    HttpMethod httpMethod;
    RequestFactory requestFactory;
    okhttp3.Call.Factory okhttpCallFactory;
    okhttp3.Call okhttpCall;

    RealCallTask(HttpMethod httpMethod, RequestFactory requestFactory, okhttp3.Call.Factory okhttpCallFactory) {
        this.httpMethod = httpMethod;
        this.requestFactory = requestFactory;
        this.okhttpCallFactory = okhttpCallFactory;
    }

    @Override
    public T execute() throws IOException {
        if(okhttpCall == null) {
            okhttpCall = newOkhttpCall();
        }

        return (T) okhttpCall.execute().body().string();
    }

    @Override
    public void enqueue() {

    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    private okhttp3.Call newOkhttpCall() {
        okhttp3.Call call = okhttpCallFactory.newCall(requestFactory.create());
        if(call == null) {
            throw new NullPointerException("there is no matching call");
        }
        return call;
    }
}
