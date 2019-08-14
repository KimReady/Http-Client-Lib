package com.naver.httpclientlib;

import java.io.IOException;

import okhttp3.MediaType;

interface Converter<ReturnType, RequestType> {

    okhttp3.RequestBody convertRequestBody(MediaType contentType, RequestType requestObj) throws IOException;

    ReturnType convertResponseBody(okhttp3.ResponseBody responseBody) throws IOException;
}