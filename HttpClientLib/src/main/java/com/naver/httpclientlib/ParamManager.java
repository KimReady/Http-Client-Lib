package com.naver.httpclientlib;

import com.naver.httpclientlib.annotation.Field;
import com.naver.httpclientlib.annotation.FieldMap;
import com.naver.httpclientlib.annotation.Header;
import com.naver.httpclientlib.annotation.HeaderMap;
import com.naver.httpclientlib.annotation.Queries;
import com.naver.httpclientlib.annotation.Query;
import com.naver.httpclientlib.annotation.QueryMap;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import okhttp3.HttpUrl;

class ParamManager {
    private Map<String, String> headerParams;
    private Map<String, String> pathParams;
    private Map<String, String> queryParams;
    private Map<String, List<String>> queriesParam;
    private Object rawRequestBody;

    ParamManager() {
        headerParams = new HashMap<>();
        pathParams = new HashMap<>();
        queryParams = new HashMap<>();
        queriesParam = new HashMap<>();
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
            pathParams.remove(paramName);
        }
        if(!pathParams.isEmpty()) {
            throw new IllegalArgumentException("there are too many actual path parameters.");
        }
        return relUrl;
    }

    HttpUrl addQueryToUrl(okhttp3.HttpUrl.Builder urlBuilder) {
        Set<String> queryNames = queriesParam.keySet();
        for(String name : queryNames) {
            List<String> queries = queriesParam.get(name);
            for(String query : queries) {
                urlBuilder.addEncodedQueryParameter(name, query);
            }
        }

        queryNames = queryParams.keySet();
        for(String name : queryNames) {
            urlBuilder.addEncodedQueryParameter(name, String.valueOf(queryParams.get(name)));
        }

        return urlBuilder.build();
    }

    void addHeadersToRequest(okhttp3.Request.Builder requestBuilder) {
        Set<String> keySet = headerParams.keySet();
        for(String key : keySet) {
            requestBuilder.addHeader(key, headerParams.get(key));
        }
    }

    void addHeader(Annotation annotation, Object arg) {
        if(annotation instanceof Header) {
            addHeaderParam(((Header) annotation).value(), arg);
        } else if(annotation instanceof HeaderMap) {
            if (!(arg instanceof Map)) {
                throw new IllegalArgumentException("The type of the '@HeaderMap' must be a Map");
            }
            Map<String, Object> headerMap = (Map) arg;
            Set<String> keySet = headerMap.keySet();
            for (String key : keySet) {
                addHeaderParam(key, headerMap.get(key));
            }
        }
    }

    void addQuery(Annotation annotation, Object arg) {
        if(annotation instanceof Query) {
            Query queryAnnotation = (Query) annotation;
            String encodedQuery = Utils.encodeQuery(arg, queryAnnotation.encodeType(), queryAnnotation.encoded());
            addQueryParam(queryAnnotation.value(), encodedQuery);
        } else if(annotation instanceof Queries) {
            Queries queriesAnnotation = (Queries) annotation;
            List<Object> queries = Utils.checkIsList(arg);
            for (Object query : queries) {
                String encodedQuery = Utils.encodeQuery(query, queriesAnnotation.encodeType(), queriesAnnotation.encoded());
                addQueriesParam(queriesAnnotation.value(), encodedQuery);
            }
        } else if(annotation instanceof QueryMap) {
            QueryMap querymapAnnotation = (QueryMap) annotation;
            if (!(arg instanceof Map)) {
                throw new IllegalArgumentException("The type of the '@QueryMap' must be a Map");
            }
            Map<String, Object> headerMap = (Map) arg;
            Set<String> keySet = headerMap.keySet();
            for (String key : keySet) {
                Object encodedQuery = Utils.encodeQuery(headerMap.get(key), querymapAnnotation.encodeType(), querymapAnnotation.encoded());
                addQueryParam(key, encodedQuery);
            }
        }
    }

    void addField(Annotation annotation, Object arg, okhttp3.FormBody.Builder formBuilder) {
        if(annotation instanceof Field) {
            if (((Field) annotation).encoded()) {
                formBuilder.addEncoded(((Field) annotation).value(), String.valueOf(arg));
            } else {
                formBuilder.add(((Field) annotation).value(), String.valueOf(arg));
            }
        } else if (annotation instanceof FieldMap) {
            if (!(arg instanceof Map)) {
                throw new IllegalArgumentException("The type of the '@FieldMap' must be a Map");
            }
            Map<String, Object> fieldMap = (Map) arg;
            Set<String> keySet = fieldMap.keySet();
            boolean encoded = (((FieldMap) annotation).encoded());
            for (String key : keySet) {
                if (encoded) {
                    formBuilder.add(key, String.valueOf(fieldMap.get(key)));
                } else {
                    formBuilder.addEncoded(key, String.valueOf(fieldMap.get(key)));
                }
            }
        }
    }

    void addHeaderParam(String key, Object value) {
        Utils.checkValidParam(key, value);
        headerParams.put(key, String.valueOf(value));
    }

    void addPathParam(String key, Object value) {
        Utils.checkValidParam(key, value);
        if(pathParams.containsKey(key)) {
            throw new IllegalArgumentException("Duplicate key value of '" + key + "'");
        }
        pathParams.put(key, String.valueOf(value));
    }

    private void addQueryParam(String key, Object value) {
        Utils.checkValidParam(key, value);
        queryParams.put(key, String.valueOf(value));
    }


    void addQueriesParam(String key, String value) {
        if(!queriesParam.containsKey(key)) {
            List<String> values = new ArrayList<>();
            values.add(value);
            queriesParam.put(key, values);
        } else {
            List<String> values = queriesParam.get(key);
            values.add(value);
        }
    }

    void setRawRequestBody(Object body) {
        rawRequestBody = body;
    }

    Object getRawRequestBody() {
        return rawRequestBody;
    }

    Map<String, List<String>> queriesParam() {
        return queriesParam;
    }
}
