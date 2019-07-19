package com.naver.httpclientlib;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class HttpInvocationHandler implements InvocationHandler {
    private final HttpClient httpClient;

    HttpInvocationHandler(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {

        // Object class에서 정의된 메소드일 경우 해당 메소드 그대로 실행
        if(method.getDeclaringClass() == Object.class) {
            return method.invoke(this, objects);
        }

        Utils.checkSupportedMethod(method);

        return HttpMethod.create(httpClient, method, objects).invoke();
    }
}
