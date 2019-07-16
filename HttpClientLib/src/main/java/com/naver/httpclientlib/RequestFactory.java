package com.naver.httpclientlib;

import com.naver.httpclientlib.annotation.Field;
import com.naver.httpclientlib.annotation.FieldMap;
import com.naver.httpclientlib.annotation.FormUrlEncoded;
import com.naver.httpclientlib.annotation.Header;
import com.naver.httpclientlib.annotation.HeaderMap;
import com.naver.httpclientlib.annotation.PathParam;
import com.naver.httpclientlib.annotation.Query;
import com.naver.httpclientlib.annotation.QueryMap;
import com.naver.httpclientlib.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;

public class RequestFactory {
    private HttpUrl baseUrl;
    private boolean hasBody;
    private boolean isFormEncoded;
    private boolean isMultipart;
    String httpMethod;
    String relativeUrl;
    List<String> pathParams;
    Headers headers;
    MediaType contentType;
    ParamManager paramManager;

    private okhttp3.Request.Builder okRequestBuilder;
    private okhttp3.FormBody.Builder formBuilder;
    private okhttp3.MultipartBody.Builder multipartBuilder;
    private okhttp3.RequestBody requestBody;

    RequestFactory(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.relativeUrl = builder.relativeUrl;
        this.hasBody = builder.hasBody;
        this.isFormEncoded = builder.isFormEncoded;
        this.httpMethod = builder.httpMethod;
        this.pathParams = builder.pathParams;
        this.headers = builder.headers;
        this.contentType = builder.contentType;
        this.isMultipart = builder.isMultipart;
        this.paramManager = builder.parameterManager;
        this.okRequestBuilder = new okhttp3.Request.Builder();
        if (isFormEncoded) {
            formBuilder = new okhttp3.FormBody.Builder();
        }
        if (isMultipart) {
            multipartBuilder = new okhttp3.MultipartBody.Builder();
        }
    }

    okhttp3.Request create() {
        if (headers != null) {
            okRequestBuilder.headers(headers);
        }

        HttpUrl completedUrl = paramManager.addQuery(baseUrl.newBuilder(relativeUrl));
        okRequestBuilder.url(completedUrl);

        paramManager.addHeaders(okRequestBuilder);

        if(httpMethod.equals("GET")) {
            return okRequestBuilder.build();
        } else if(httpMethod.equals("DELETE")) {
            return okRequestBuilder.delete(requestBody).build();
        } else if(httpMethod.equals("POST")) {
            if (isFormEncoded) {
                requestBody = formBuilder.build();
                return okRequestBuilder.post(requestBody).build();
            } else if (hasBody) {
                return okRequestBuilder.post(requestBody).build();
            } else if (isMultipart) {
                return okRequestBuilder.post(requestBody).build();
            }
        } else if(httpMethod.equals("PUT")) {
            if (isFormEncoded) {
                requestBody = formBuilder.build();
                return okRequestBuilder.put(requestBody).build();
            } else if (hasBody) {
                return okRequestBuilder.put(requestBody).build();
            } else if (isMultipart) {
                return okRequestBuilder.put(requestBody).build();
            }
        } else if(httpMethod.equals("HEAD")) {
            return okRequestBuilder.head().build();
        }
        return null;
    }

    public static class Builder {
        private Method method;
        private Annotation[] methodAnnotations;
        private Annotation[][] parameterAnnotations;
        private ParamManager parameterManager;
        private okhttp3.Call.Factory callFactory;

        private HttpUrl baseUrl;
        private boolean hasBody;
        private boolean isFormEncoded;
        private boolean isMultipart;
        private String httpMethod;
        private String relativeUrl;
        private List<String> pathParams;
        private Headers headers;
        private MediaType contentType;
        private Object[] args;

        public Builder(HttpClient httpClient, Method method, Object[] args) {
            this.callFactory = httpClient.getCallFactory();
            this.method = method;
            this.baseUrl = httpClient.getBaseUrl();
            this.methodAnnotations = method.getAnnotations();
            this.parameterManager = new ParamManager();
            this.parameterAnnotations = method.getParameterAnnotations();
            this.isMultipart = false;
            this.args = args;
        }

        public RequestFactory build() {
            for (Annotation annotation : methodAnnotations) {
                parseMethodAnnotation(annotation);
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
                isFormEncoded = true;
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
                this.pathParams = parsePathParameters(relUrl);
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

        /**
         * Relative URL에서 Path Parameter 파싱
         *
         * @param relUrl Relative URL
         * @return path parameter list
         */
        private List<String> parsePathParameters(String relUrl) {
            List<String> pathParameters = new LinkedList<>();
            Matcher matcher = Utils.matchPathUrl(relUrl);
            while (matcher.find()) {
                String param = matcher.group();
                pathParameters.add(param.substring(1, param.length() - 1));
            }
            return pathParameters;
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
                Object encodedQuery = Utils.encodeQuery(arg, ((Query) annotation).encoded());
                parameterManager.addQueryParam(((Query) annotation).value(), encodedQuery);
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
                parameterManager.addFieldParam(((Field) annotation).value(), arg);
            } else if (annotation instanceof FieldMap) {
                if (!(arg instanceof Map)) {
                    throw new IllegalArgumentException("The type of the '@FieldMap' must be a Map");
                }
                Map<String, Object> headerMap = (Map) arg;
                Set<String> keySet = headerMap.keySet();
                for (String key : keySet) {
                    parameterManager.addFieldParam(key, headerMap.get(key));
                }
            }
        }

    }
}
