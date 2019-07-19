package com.naver.httpclientlib;

import com.naver.httpclientlib.converter.GsonConverterFactory;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.naver.httpclientlib.RequestMethod.HEAD;

class HttpMethod<ResponseT> {

    static HttpMethod create(HttpClient httpClient, Method method, Object[] args) {
        RequestFactory requestFactory = new RequestFactory(httpClient, method, args).initialize();

        Type returnType = method.getGenericReturnType();
        Type responseType = Utils.getParameterUpperBound(0, (ParameterizedType) returnType);
        if(requestFactory.httpMethod() == HEAD && !Void.class.equals(responseType)) {
            throw new IllegalArgumentException("HEAD method must use Void as response type.");
        }

        if(httpClient.getConverter() == null) {
            httpClient.setConverter(GsonConverterFactory.create().converter(responseType));
        }

        return new HttpMethod<>(httpClient, requestFactory);
    }

    private HttpClient httpClient;
    private RequestFactory requestFactory;

    private HttpMethod(HttpClient httpClient, RequestFactory requestFactory) {
        this.httpClient = httpClient;
        this.requestFactory = requestFactory;
    }

    CallTask<ResponseT> invoke() {
        return new RealCallTask<>(requestFactory, httpClient.getCallFactory(), httpClient.getConverter());
    }
}
