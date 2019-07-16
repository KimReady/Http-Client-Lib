package com.naver.httpclientlib;

import com.naver.httpclientlib.converter.Converter;
import com.naver.httpclientlib.converter.GsonConverterFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

class HttpMethod<ResponseT, ReturnT> {

    static HttpMethod create(HttpClient httpClient, Method method, Object[] args) {
        RequestFactory requestFactory = new RequestFactory.Builder(httpClient, method, args).build();

        Type adapterType = method.getGenericReturnType();

        CallAdapter<?, ?> callAdapter =  CallAdapterFactory.create(adapterType);
        Type responseType = callAdapter.responseType();
        if(requestFactory.httpMethod.equals("HEAD") && !Void.class.equals(responseType)) {
            throw new IllegalArgumentException("HEAD method must use Void as response type.");
        }

        if(httpClient.getConverter() == null) {
            httpClient.setConverter(GsonConverterFactory.create().converter(responseType));
        }

        return new HttpMethod<>(httpClient, requestFactory, method, args);
    }

    private HttpClient httpClient;
    private RequestFactory requestFactory;
    private Method method;
    private Object[] args;

    private HttpMethod(HttpClient httpClient, RequestFactory requestFactory, Method method, Object[] args) {
        this.httpClient = httpClient;
        this.requestFactory = requestFactory;
        this.method = method;
        this.args = args;
    }

    public CallTask<ResponseT> invoke() {
        return new RealCallTask<ResponseT>(this,
                requestFactory, httpClient.getCallFactory(), httpClient.getConverter());
    }
}
