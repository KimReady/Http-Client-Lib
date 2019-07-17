package com.naver.httpclientlib.converter;

import java.io.IOException;

import okhttp3.MediaType;

public interface Converter<ReturnType, RequestType> {

    okhttp3.RequestBody convertRequestBody(MediaType contentType, RequestType requestObj) throws IOException;

    ReturnType convertResponseBody(okhttp3.ResponseBody responseBody) throws IOException;
}