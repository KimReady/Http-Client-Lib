package com.naver.httpclientlib;

import com.naver.httpclientlib.converter.Converter;
import com.naver.httpclientlib.converter.GsonConverterFactory;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;

import static com.naver.httpclientlib.RequestMethod.HEAD;

class HttpMethod<T> {
    static HttpMethod create(HttpClient httpClient, Method method, Object[] args) {
        RequestFactory requestFactory = new RequestFactory(httpClient.getBaseUrl(), method, args).initialize();

        Type returnType = method.getGenericReturnType();
        Type responseType = Utils.getParameterUpperBound(0, (ParameterizedType) returnType);
        if(requestFactory.httpMethod() == HEAD && !Void.class.equals(responseType)) {
            throw new IllegalArgumentException("HEAD method must use Void as response type.");
        }

        return new HttpMethod<>(httpClient, requestFactory, responseType);
    }

    private okhttp3.Call.Factory callFactory;
    private RequestFactory requestFactory;
    private Converter converter;
    private ExecutorService executorService;

    private HttpMethod(HttpClient httpClient, RequestFactory requestFactory, Type responseType) {
        this.callFactory = httpClient.getCallFactory();
        this.requestFactory = requestFactory;
        this.converter = GsonConverterFactory.create().converter(responseType);
        this.executorService = httpClient.getExecutorService();
    }

    CallTask<T> invoke() {
        return new RealCallTask<>(requestFactory, callFactory, converter, executorService);
    }
}
