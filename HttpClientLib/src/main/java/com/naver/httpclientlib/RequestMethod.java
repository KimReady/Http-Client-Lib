package com.naver.httpclientlib;

public enum RequestMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    HEAD("HEAD");

    private String name;

    RequestMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
