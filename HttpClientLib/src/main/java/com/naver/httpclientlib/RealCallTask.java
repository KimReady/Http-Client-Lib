package com.naver.httpclientlib;

import com.naver.httpclientlib.converter.Converter;

import java.io.IOException;

class RealCallTask<T> implements CallTask<T> {
    HttpMethod httpMethod;
    RequestFactory requestFactory;
    okhttp3.Call.Factory okhttpCallFactory;
    okhttp3.Call okhttpCall;
    Converter<T, ?> converter;

    RealCallTask(HttpMethod httpMethod, RequestFactory requestFactory,
                 okhttp3.Call.Factory okhttpCallFactory, Converter<T, ?> converter) {
        this.httpMethod = httpMethod;
        this.requestFactory = requestFactory;
        this.okhttpCallFactory = okhttpCallFactory;
        this.converter = converter;
    }

    @Override
    public Response<T> execute() throws IOException {
        if(okhttpCall == null) {
            okhttpCall = newOkhttpCall();
        }

        return convertResponse(okhttpCall.execute());
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

    private Response<T> convertResponse(okhttp3.Response response) {
        return new Response<>(response, converter);
    }
}
