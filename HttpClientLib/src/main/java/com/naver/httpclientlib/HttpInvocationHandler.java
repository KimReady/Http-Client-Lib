package com.naver.httpclientlib;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

class HttpInvocationHandler implements InvocationHandler {
    final HttpClient httpClient;

    public HttpInvocationHandler(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {

        // Object class에서 정의된 메소드일 경우 해당 메소드 그대로 실행
        if(method.getDeclaringClass() == Object.class) {
            return method.invoke(this, objects);
        }

        // @RequestMapping 어노테이션이 적용되지 않은 메소드의 경우 Exception 처리
        Utils.checkSupportedMethod(method);

        return HttpMethod.create(httpClient, method, objects).invoke();
    }
}
