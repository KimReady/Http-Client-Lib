package com.naver.httpclientlib;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;

import static com.naver.httpclientlib.RequestMethod.HEAD;

class HttpMethod<T> {
    static HttpMethod of(HttpClient httpClient, Method method, Object[] args) {
        RequestFactory requestFactory = new RequestFactory(httpClient.getBaseUrl(), method, args).initialize();

        Type returnType = method.getGenericReturnType();
        Type responseType = Utils.getParameterUpperBound(0, (ParameterizedType) returnType);
        if(requestFactory.httpMethod() == HEAD && !Void.class.equals(responseType)) {
            throw new IllegalArgumentException("HEAD method must use Void as response type.");
        }

        return new HttpMethod<>(httpClient, requestFactory, responseType);
    }

    private final okhttp3.Call.Factory callFactory;
    private final RequestFactory requestFactory;
    private final Converter converter;
    private final ExecutorService executorService;

    private HttpMethod(HttpClient httpClient, RequestFactory requestFactory, Type responseType) {
        this.callFactory = httpClient.getCallFactory();
        this.requestFactory = requestFactory;
        this.converter = requestFactory.httpMethod() != RequestMethod.HEAD ?
                GsonConverterFactory.create(httpClient.gsonBuilder()).converter(responseType)
                : null;
        this.executorService = httpClient.getExecutorService();
    }

    CallTask<T> invoke() {
        return new RealCallTask<>(requestFactory, callFactory, converter, executorService);
    }
}
