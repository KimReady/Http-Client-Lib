package com.naver.httpclienttest;

enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    HEAD("HEAD");

    String name;

    HttpMethod(String method) {
        name = method;
    }

    String getName() {
        return name;
    }
}
