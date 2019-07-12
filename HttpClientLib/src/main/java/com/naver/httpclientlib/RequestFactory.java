package com.naver.httpclientlib;

import com.naver.httpclientlib.annotation.FormUrlEncoded;
import com.naver.httpclientlib.annotation.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;

public class RequestFactory {
    public Method method;
    public boolean hasBody;
    public boolean isFormEncoded;
    public String httpMethod;
    public String relativeUrl;
    public List<String> pathParams;
    public Headers headers;
    public MediaType contentType;
    okhttp3.Request.Builder okhttpRequestBuilder;

    RequestFactory(Builder builder) {
        this.relativeUrl = builder.relativeUrl;
        this.method = builder.method;
        this.hasBody = builder.hasBody;
        this.isFormEncoded = builder.isFormEncoded;
        this.httpMethod = builder.httpMethod;
        this.pathParams = builder.pathParams;
        this.headers = builder.headers;
        this.contentType = builder.contentType;
        this.okhttpRequestBuilder = builder.okhttpRequestBuilder;
    }

    okhttp3.Request create() {
        return null;
    }

    public static class Builder {
        private static final Pattern PATH_PARAM_URL_REG = Pattern.compile("\\{[a-zA-Z][a-zA-Z0-9_-]*}");
        HttpClient httpClient;
        Method method;
        Annotation[] methodAnnotations;
        Annotation[][] parameterAnnotations;
        Type[] parameterTypes;
        okhttp3.Request.Builder okhttpRequestBuilder;
        ParamManager[] parameterManager;

        boolean hasBody;
        boolean isFormEncoded;
        String httpMethod;
        String relativeUrl;
        List<String> pathParams;
        Headers headers;
        MediaType contentType;

        public Builder(HttpClient httpClient, Method method) {
            this.httpClient = httpClient;
            this.method = method;
            this.methodAnnotations = method.getAnnotations();
            this.parameterTypes = method.getGenericParameterTypes();
            this.parameterAnnotations = method.getParameterAnnotations();
            this.okhttpRequestBuilder = new okhttp3.Request.Builder();
        }

        public RequestFactory build() {
            for(Annotation annotation : methodAnnotations) {
                parseMethodAnnotation(annotation);
            }

            int paramCount = parameterAnnotations.length;
            parameterManager = new ParamManager[paramCount];
            for(int i = 0; i < paramCount; i++) {
                if(parameterAnnotations[i] != null) {
                    for(Annotation annotation : parameterAnnotations[i]) {

                    }
                }
            }

            return new RequestFactory(this);
        }

        /**
         * parse a annotation of the method
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
         * @param httpMethod
         * @param relUrl    Relative URL
         * @param hasBody   whether having a request body
         */
        private void parseHttpMethodAndPath(String httpMethod, String relUrl, boolean hasBody) {
            if (this.httpMethod != null) {
                throw new IllegalArgumentException("Only one HTTP method is allowed.");
            }
            this.httpMethod = httpMethod;
            this.hasBody = hasBody;

            if (!relUrl.isEmpty()) {
                this.relativeUrl = relUrl;
                this.pathParams = parsePathParameters(relUrl);
            }
        }

        /**
         * '@Headers' annotation values parsing
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
         * @param relUrl Relative URL
         * @return path parameter list
         */
        private List<String> parsePathParameters(String relUrl) {
            List<String> pathParameters = new LinkedList<>();
            Matcher matcher = PATH_PARAM_URL_REG.matcher(relUrl);
            while(matcher.find()) {
                String param = matcher.group();
                pathParameters.add(param.substring(1, param.length() - 1));
            }
            return pathParameters;
        }

    }
}
