package com.naver.httpclientlib;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import okhttp3.HttpUrl;

class ParamManager {
    private Map<String, String> headerParams;
    private Map<String, String> pathParams;
    private Map<String, String> queryParams;
    private Map<String, String> fieldParams;

    ParamManager() {
        headerParams = new HashMap<>();
        pathParams = new HashMap<>();
        queryParams = new HashMap<>();
        fieldParams = new HashMap<>();
    }

    String replacePathParameters(String relUrl) {
        Matcher matcher = Utils.matchPathUrl(relUrl);
        while (matcher.find()) {
            String pathParam = matcher.group();
            String paramName = pathParam.substring(1, pathParam.length()-1);
            if(!pathParams.containsKey(paramName)) {
                throw new IllegalArgumentException("there is no matching parameter to '" + paramName + "'.");
            }
            relUrl = relUrl.replace(pathParam, pathParams.get(paramName));
        }
        return relUrl;
    }

    HttpUrl addQuery(okhttp3.HttpUrl.Builder urlBuilder) {
        Set<String> queryNames = queryParams.keySet();
        for(String name : queryNames) {
            urlBuilder.addEncodedQueryParameter(name, queryParams.get(name));
        }
        return urlBuilder.build();
    }

    void addHeaders(okhttp3.Request.Builder requestBuilder) {
        Set<String> keySet = headerParams.keySet();
        for(String key : keySet) {
            requestBuilder.addHeader(key, headerParams.get(key));
        }
    }

    void addHeaderParam(String key, Object value) {
        Utils.checkValidParam(key, value);
        headerParams.put(key, String.valueOf(value));
    }

    void addPathParam(String key, Object value) {
        Utils.checkValidParam(key, value);
        pathParams.put(key, String.valueOf(value));
    }

    void addQueryParam(String key, Object value) {
        Utils.checkValidParam(key, value);
        queryParams.put(key, String.valueOf(value));
    }

    void addFieldParam(String key, Object value) {
        Utils.checkValidParam(key, value);
        fieldParams.put(key, String.valueOf(value));
    }

    Map<String, String> getPathParams() {
        return pathParams;
    }

    public Map<String, String> getHeaderParams() {
        return headerParams;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public Map<String, String> getFieldParams() {
        return fieldParams;
    }
}
