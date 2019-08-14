package com.naver.httpclientlib;

import java.io.IOException;

public interface Interceptor {
    Response intercept(InterceptorChain chain) throws IOException;
}
