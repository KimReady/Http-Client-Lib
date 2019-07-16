package com.naver.httpclientlib.converter;

import com.google.gson.Gson;

import java.io.IOException;

public interface Converter<ReturnType, RequestType> {

    okhttp3.RequestBody convertRequestBody(RequestType requestObj) throws IOException;

    ReturnType convertResponseBody(okhttp3.ResponseBody responseBody) throws IOException;
}