package com.naver.httpclientlib;

import com.naver.httpclientlib.annotation.DynamicURL;
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
import com.naver.httpclientlib.annotation.URL;
import com.naver.httpclientlib.converter.Converter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;

class RequestFactory {
    private final Annotation[] methodAnnotations;
    private final Annotation[][] parameterAnnotations;
    private ParamManager parameterManager;

    private final HttpUrl baseUrl;
    private boolean hasBody;
    private boolean isFormEncoded;
    private RequestMethod httpMethod;
    private String relativeUrl;
    private HttpUrl completedUrl;
    private Headers headers;
    private MediaType contentType;
    private final Object[] args;
    private boolean hasUrl;
    private boolean isDynamicUrl;

    private okhttp3.Request.Builder okRequestBuilder;
    private okhttp3.FormBody.Builder formBuilder;
    private okhttp3.RequestBody requestBody;

    RequestFactory(HttpUrl baseUrl, Method method, Object[] args) {
        this.baseUrl = baseUrl;
        this.methodAnnotations = method.getAnnotations();
        this.parameterAnnotations = method.getParameterAnnotations();
        this.args = args;
        this.parameterManager = new ParamManager();
        this.okRequestBuilder = new okhttp3.Request.Builder();
    }

    /**
     * Method와 Prameter에 있는 어노테이션과 객체들을 Parsing
     * @return
     */
    RequestFactory initialize() {
        // method 단위 어노테이션 parsing
        for (Annotation annotation : methodAnnotations) {
            parseMethodAnnotation(annotation);
        }

        // parameter 단위 어노테이션 parsing
        int paramCount = parameterAnnotations.length;
        for (int i = 0; i < paramCount; i++) {
            if (parameterAnnotations[i] != null && args[i] != null) {
                for (Annotation annotation : parameterAnnotations[i]) {
                    parseParameterManager(annotation, args[i]);
                }
            }
        }

        if (!isDynamicUrl) {
            Utils.checkNotNull(baseUrl, "if you don't use @DynamicURL, you need to set the baseURL when creating the HttpClient Object.");
            this.relativeUrl = replacePathParameters(relativeUrl);
            completedUrl = parameterManager.addQueryToUrl(baseUrl.newBuilder(relativeUrl));
        } else {
            completedUrl = parameterManager.addQueryToUrl(completedUrl.newBuilder());
        }

        return this;
    }

    /**
     * okhttp Request Builder를 통해 Request 생성
     * @return Request object
     * @throws IOException RequestBody를 Convert 하는 과정에서 발생할 수 있는 IOException
     */
    okhttp3.Request create(Converter converter) throws IOException {
        if (headers != null) {
            okRequestBuilder.headers(headers);
        }
        okRequestBuilder.url(completedUrl);
        parameterManager.addHeadersToRequest(okRequestBuilder);

        if (isFormEncoded) {
            requestBody = formBuilder.build();
        } else if (hasBody) {
            Object rawRequestBody = parameterManager.getRawRequestBody();
            if (rawRequestBody == null) {
                throw new IllegalArgumentException("method " + httpMethod + " must have a request body.");
            }
            requestBody = converter.convertRequestBody(contentType, rawRequestBody);
        }

        return okRequestBuilder.method(httpMethod.getName(), requestBody).build();
    }

    /**
     * parse a annotation of the method
     * @param annotation
     */
    private void parseMethodAnnotation(Annotation annotation) {
        if (annotation instanceof RequestMapping || annotation instanceof DynamicURL) {
            if (hasUrl) {
                throw new IllegalArgumentException("You must use only one on your method, either @RequestMapping or @DynamicURL.");
            }
            hasUrl = true;
            isDynamicUrl = annotation instanceof DynamicURL;
            RequestMethod requestMethod = (annotation instanceof RequestMapping) ?
                    ((RequestMapping) annotation).method() :
                    ((DynamicURL) annotation).method();

            String relUrl = (annotation instanceof RequestMapping) ?
                    ((RequestMapping) annotation).value() : null;

            switch(requestMethod) {
                case GET:
                case DELETE:
                case HEAD:
                    parseHttpMethodAndPath(requestMethod, relUrl, false);
                    break;
                case POST:
                case PUT:
                    parseHttpMethodAndPath(requestMethod, relUrl, true);
                    break;
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
     * @param httpMethod
     * @param relUrl     Relative URL
     * @param hasBody    whether having a request body
     */
    private void parseHttpMethodAndPath(RequestMethod httpMethod, String relUrl, boolean hasBody) {
        this.httpMethod = httpMethod;
        this.hasBody = hasBody;

        if (relUrl != null) {
            this.relativeUrl = relUrl;
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

    String replacePathParameters(String relUrl) {
        return parameterManager.replacePathParameters(relUrl);
    }

    private void parseParameterManager(Annotation annotation, Object arg) {

        if (annotation instanceof Header || annotation instanceof HeaderMap) {
            parameterManager.addHeader(annotation, arg);
        } else if (annotation instanceof PathParam) {
            Utils.checkIsFalse(isDynamicUrl, "You can't use @PathParam with @DynamicURL.");
            parameterManager.addPathParam(((PathParam) annotation).value(), arg);
        } else if (annotation instanceof Query || annotation instanceof Queries || annotation instanceof QueryMap) {
            parameterManager.addQuery(annotation, arg);
        } else if (annotation instanceof Field || annotation instanceof FieldMap) {
            Utils.checkIsTrue(isFormEncoded, "'@Field' needs a '@FormUrlEncoded' on your method.");
            parameterManager.addField(annotation, arg, formBuilder);
        } else if (annotation instanceof RequestBody) {
            Utils.checkIsTrue(hasBody, httpMethod.getName() + " method cannot have a request body.");
            parameterManager.setRawRequestBody(arg);
        } else if (annotation instanceof URL) {
            boolean isUrl = (arg instanceof String || arg instanceof java.net.URL
                    || arg instanceof URI || arg instanceof HttpUrl);
            Utils.checkIsTrue(isUrl, "The @URL value must be of type String, URL, URI, or HttpUrl.");

            if (completedUrl != null) {
                throw new IllegalArgumentException("You can use @URL once.");
            }
            completedUrl = Utils.getHttpUrl(arg);
        }
    }

    RequestMethod httpMethod() {
        return httpMethod;
    }
}
