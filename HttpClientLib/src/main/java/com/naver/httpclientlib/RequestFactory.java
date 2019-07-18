package com.naver.httpclientlib;

import com.naver.httpclientlib.annotation.Field;
import com.naver.httpclientlib.annotation.FieldMap;
import com.naver.httpclientlib.annotation.FormUrlEncoded;
import com.naver.httpclientlib.annotation.Header;
import com.naver.httpclientlib.annotation.HeaderMap;
import com.naver.httpclientlib.annotation.PathParam;
import com.naver.httpclientlib.annotation.Queries;
import com.naver.httpclientlib.annotation.Query;
import com.naver.httpclientlib.annotation.QueryMap;
import com.naver.httpclientlib.annotation.RequestBody;
import com.naver.httpclientlib.annotation.RequestMapping;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;

class RequestFactory {
    private final HttpClient httpClient;
    private final HttpUrl baseUrl;
    private final boolean hasBody;
    private final boolean isFormEncoded;
    private final boolean isMultipart;
    private final Headers headers;
    private final MediaType contentType;
    private final ParamManager paramManager;
    final String httpMethod;
    final String relativeUrl;

    private okhttp3.Request.Builder okRequestBuilder;
    private okhttp3.FormBody.Builder formBuilder;
    private okhttp3.MultipartBody.Builder multipartBuilder;
    private okhttp3.RequestBody requestBody;

    RequestFactory(Builder builder) {
        this.httpClient = builder.httpClient;
        this.baseUrl = builder.baseUrl;
        this.relativeUrl = builder.relativeUrl;
        this.hasBody = builder.hasBody;
        this.isFormEncoded = builder.isFormEncoded;
        this.httpMethod = builder.httpMethod;
        this.headers = builder.headers;
        this.contentType = builder.contentType;
        this.isMultipart = builder.isMultipart;
        this.paramManager = builder.parameterManager;
        this.okRequestBuilder = builder.okRequestBuilder;
        this.formBuilder = builder.formBuilder;
        this.multipartBuilder = builder.multipartBuilder;
    }

    okhttp3.Request create() throws IOException {
        if (headers != null) {
            okRequestBuilder.headers(headers);
        }

        HttpUrl completedUrl = paramManager.addQuery(baseUrl.newBuilder(relativeUrl));
        okRequestBuilder.url(completedUrl);

        paramManager.addHeaders(okRequestBuilder);

        if (isFormEncoded) {
            requestBody = formBuilder.build();
        } else if(hasBody){
            Object rawRequestBody = paramManager.getRawRequestBody();
            if(rawRequestBody == null) {
                throw new IllegalArgumentException("method " + httpMethod + " must have a request body.");
            }
            requestBody = httpClient.getConverter().convertRequestBody(contentType, rawRequestBody);
        }

        return okRequestBuilder.method(httpMethod, requestBody).build();
    }

    public static class Builder {
        private final HttpClient httpClient;
        private final Annotation[] methodAnnotations;
        private final Annotation[][] parameterAnnotations;
        private ParamManager parameterManager;

        private final HttpUrl baseUrl;
        private boolean hasBody;
        private boolean isFormEncoded;
        private boolean isMultipart;
        private String httpMethod;
        private String relativeUrl;
        private Headers headers;
        private MediaType contentType;
        private final Object[] args;

        private okhttp3.Request.Builder okRequestBuilder;
        private okhttp3.FormBody.Builder formBuilder;
        private okhttp3.MultipartBody.Builder multipartBuilder;

        public Builder(HttpClient httpClient, Method method, Object[] args) {
            this.httpClient = httpClient;
            this.baseUrl = httpClient.getBaseUrl();
            this.methodAnnotations = method.getAnnotations();
            this.parameterManager = new ParamManager();
            this.parameterAnnotations = method.getParameterAnnotations();
            this.isMultipart = false;
            this.args = args;
            this.okRequestBuilder = new okhttp3.Request.Builder();
        }

        public RequestFactory build() {
            for (Annotation annotation : methodAnnotations) {
                parseMethodAnnotation(annotation);
            }

            if (isMultipart) {
                multipartBuilder = new okhttp3.MultipartBody.Builder();
            }

            int paramCount = parameterAnnotations.length;
            for (int i = 0; i < paramCount; i++) {
                if (parameterAnnotations[i] != null && args[i] != null) {
                    for (Annotation annotation : parameterAnnotations[i]) {
                        parseParameterManager(annotation, args[i]);
                    }
                }
            }

            this.relativeUrl = replacePathParameters(relativeUrl);

            return new RequestFactory(this);
        }

        /**
         * parse a annotation of the method
         *
         * @param annotation
         */
        private void parseMethodAnnotation(Annotation annotation) {
            if (annotation instanceof RequestMapping) {
                if (((RequestMapping) annotation).method() == RequestMethod.GET) {
                    parseHttpMethodAndPath("GET", ((RequestMapping) annotation).value(), false);
                } else if (((RequestMapping) annotation).method() == RequestMethod.DELETE) {
                    parseHttpMethodAndPath("DELETE", ((RequestMapping) annotation).value(), false);
                } else if (((RequestMapping) annotation).method() == RequestMethod.HEAD) {
                    parseHttpMethodAndPath("HEAD", ((RequestMapping) annotation).value(), false);
                } else if (((RequestMapping) annotation).method() == RequestMethod.POST) {
                    parseHttpMethodAndPath("POST", ((RequestMapping) annotation).value(), true);
                } else if (((RequestMapping) annotation).method() == RequestMethod.PUT) {
                    parseHttpMethodAndPath("PUT", ((RequestMapping) annotation).value(), true);
                }
            } else if (annotation instanceof com.naver.httpclientlib.annotation.Headers) {
                String[] headersToParse = ((com.naver.httpclientlib.annotation.Headers) annotation).value();
                if (headersToParse.length == 0) {
                    throw new IllegalArgumentException("@Headers annotation is empty.");
                }
                headers = parseHeaders(headersToParse);
            } else if (annotation instanceof FormUrlEncoded) {
                formBuilder = new okhttp3.FormBody.Builder();
                isFormEncoded = true;
                contentType = MediaType.parse("application/x-www-form-urlencoded");
            }
        }

        /**
         * Http Method, Relative URL, path parameters parsing
         *
         * @param httpMethod
         * @param relUrl     Relative URL
         * @param hasBody    whether having a request body
         */
        private void parseHttpMethodAndPath(String httpMethod, String relUrl, boolean hasBody) {
            if (this.httpMethod != null) {
                throw new IllegalArgumentException("Only one HTTP method is allowed.");
            }
            this.httpMethod = httpMethod;
            this.hasBody = hasBody;

            if (!relUrl.isEmpty()) {
                this.relativeUrl = relUrl;
            }
        }

        /**
         * '@Headers' annotation values parsing
         *
         * @param headers
         * @return object of okhttp3.Headers
         */
        private Headers parseHeaders(String[] headers) {
            Headers.Builder headerBuilder = new Headers.Builder();
            for (String header : headers) {
                int colon = header.indexOf(':');
                String headerName = header.substring(0, colon);
                String headerValue = header.substring(colon + 1).trim();
                if ("Content-Type".equalsIgnoreCase(headerName)) {
                    contentType = MediaType.get(headerValue);
                } else {
                    headerBuilder.add(headerName, headerValue);
                }
            }
            return headerBuilder.build();
        }

        String replacePathParameters(String relUrl) {
            return parameterManager.replacePathParameters(relUrl);
        }

        private void parseParameterManager(Annotation annotation, Object arg) {
            if (annotation instanceof Header) {
                parameterManager.addHeaderParam(((Header) annotation).value(), arg);
            } else if (annotation instanceof HeaderMap) {
                if (!(arg instanceof Map)) {
                    throw new IllegalArgumentException("The type of the '@HeaderMap' must be a Map");
                }
                Map<String, Object> headerMap = (Map) arg;
                Set<String> keySet = headerMap.keySet();
                for (String key : keySet) {
                    parameterManager.addHeaderParam(key, headerMap.get(key));
                }
            } else if (annotation instanceof PathParam) {
                parameterManager.addPathParam(((PathParam) annotation).value(), arg);
            } else if (annotation instanceof Query) {
                String encodedQuery = Utils.encodeQuery(arg, ((Query) annotation).encoded());
                parameterManager.addQueryParam(((Query) annotation).value(), encodedQuery);
            } else if (annotation instanceof Queries) {
                List<Object> queries = Utils.checkIsList(arg);
                for(Object query : queries) {
                    String encodedQuery = Utils.encodeQuery(query, ((Queries) annotation).encoded());
                    parameterManager.addQueriesParam(((Queries) annotation).value(), encodedQuery);
                }
            } else if (annotation instanceof QueryMap) {
                if (!(arg instanceof Map)) {
                    throw new IllegalArgumentException("The type of the '@QueryMap' must be a Map");
                }
                Map<String, Object> headerMap = (Map) arg;
                Set<String> keySet = headerMap.keySet();
                for (String key : keySet) {
                    Object encodedQuery = Utils.encodeQuery(headerMap.get(key), ((QueryMap) annotation).encoded());
                    parameterManager.addQueryParam(key, encodedQuery);
                }
            } else if (annotation instanceof Field) {
                if(!isFormEncoded) {
                    throw new IllegalArgumentException("'@Field' needs a '@FormUrlEncoded' above the method.");
                }
                if(((Field) annotation).encoded()) {
                    formBuilder.addEncoded(((Field) annotation).value(), String.valueOf(arg));
                } else {
                    formBuilder.add(((Field) annotation).value(), String.valueOf(arg));
                }
            } else if (annotation instanceof FieldMap) {
                if(!isFormEncoded) {
                    throw new IllegalArgumentException("'@FieldMap' needs a '@FormUrlEncoded' above the method.");
                }
                if (!(arg instanceof Map)) {
                    throw new IllegalArgumentException("The type of the '@FieldMap' must be a Map");
                }
                Map<String, Object> fieldMap = (Map) arg;
                Set<String> keySet = fieldMap.keySet();
                boolean encoded = (((FieldMap) annotation).encoded());
                for (String key : keySet) {
                    if(encoded) {
                        formBuilder.add(key, String.valueOf(fieldMap.get(key)));
                    } else {
                        formBuilder.addEncoded(key, String.valueOf(fieldMap.get(key)));
                    }
                }
            } else if (annotation instanceof RequestBody) {
                if(!hasBody) {
                    throw new IllegalArgumentException(httpMethod + " method cannot have a request body.");
                }
                parameterManager.setRawRequestBody(arg);
            }
        }
    }
}
