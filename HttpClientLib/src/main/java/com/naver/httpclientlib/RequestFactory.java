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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;

class RequestFactory {
    private final HttpClient httpClient;
    private final boolean hasBody;
    private final boolean isFormEncoded;
    private final Headers headers;
    private final MediaType contentType;
    private final ParamManager paramManager;
    final String httpMethod;
    private final HttpUrl completedUrl;

    private okhttp3.Request.Builder okRequestBuilder;
    private okhttp3.FormBody.Builder formBuilder;
    private okhttp3.RequestBody requestBody;

    RequestFactory(Builder builder) {
        this.httpClient = builder.httpClient;
        this.hasBody = builder.hasBody;
        this.isFormEncoded = builder.isFormEncoded;
        this.httpMethod = builder.httpMethod;
        this.headers = builder.headers;
        this.contentType = builder.contentType;
        this.paramManager = builder.parameterManager;
        this.okRequestBuilder = builder.okRequestBuilder;
        this.formBuilder = builder.formBuilder;
        this.completedUrl = builder.completedUrl;
    }

    okhttp3.Request create() throws IOException {
        if (headers != null) {
            okRequestBuilder.headers(headers);
        }
        okRequestBuilder.url(completedUrl);
        paramManager.addHeaders(okRequestBuilder);

        if (isFormEncoded) {
            requestBody = formBuilder.build();
        } else if (hasBody) {
            Object rawRequestBody = paramManager.getRawRequestBody();
            if (rawRequestBody == null) {
                throw new IllegalArgumentException("method " + httpMethod + " must have a request body.");
            }
            requestBody = httpClient.getConverter().convertRequestBody(contentType, rawRequestBody);
        }

        return okRequestBuilder.method(httpMethod, requestBody).build();
    }

    static class Builder {
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
        private HttpUrl completedUrl;
        private Headers headers;
        private MediaType contentType;
        private final Object[] args;
        private boolean hasUrl;
        private boolean isDynamicUrl;

        private okhttp3.Request.Builder okRequestBuilder;
        private okhttp3.FormBody.Builder formBuilder;

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

            int paramCount = parameterAnnotations.length;
            for (int i = 0; i < paramCount; i++) {
                if (parameterAnnotations[i] != null && args[i] != null) {
                    for (Annotation annotation : parameterAnnotations[i]) {
                        parseParameterManager(annotation, args[i]);
                    }
                }
            }

            if (!isDynamicUrl) {
                this.relativeUrl = replacePathParameters(relativeUrl);
                completedUrl = parameterManager.addQuery(baseUrl.newBuilder(relativeUrl));
            }

            Utils.checkNotNull(completedUrl, "URL is Null");

            return new RequestFactory(this);
        }

        /**
         * parse a annotation of the method
         *
         * @param annotation
         */
        private void parseMethodAnnotation(Annotation annotation) {
            if (annotation instanceof RequestMapping || annotation instanceof DynamicURL) {
                if (hasUrl) {
                    throw new IllegalArgumentException("You must use only one on your method, either @RequestMapping or @DynamicURL.");
                }
                hasUrl = true;
                isDynamicUrl = (annotation instanceof DynamicURL) ? true : false;
                RequestMethod requestMethod = (annotation instanceof RequestMapping) ?
                        ((RequestMapping) annotation).method() :
                        ((DynamicURL) annotation).method();

                String relUrl = (annotation instanceof RequestMapping) ?
                        ((RequestMapping) annotation).value() : null;

                if (requestMethod == RequestMethod.GET) {
                    parseHttpMethodAndPath("GET", relUrl, false);
                } else if (requestMethod == RequestMethod.DELETE) {
                    parseHttpMethodAndPath("DELETE", relUrl, false);
                } else if (requestMethod == RequestMethod.HEAD) {
                    parseHttpMethodAndPath("HEAD", relUrl, false);
                } else if (requestMethod == RequestMethod.POST) {
                    parseHttpMethodAndPath("POST", relUrl, true);
                } else if (requestMethod == RequestMethod.PUT) {
                    parseHttpMethodAndPath("PUT", relUrl, true);
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
            this.httpMethod = httpMethod;
            this.hasBody = hasBody;

            if (relUrl != null) {
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
                if(isDynamicUrl) {
                    throw new IllegalArgumentException("You can't use @PathParam with @DynamicURL.");
                }
                parameterManager.addPathParam(((PathParam) annotation).value(), arg);
            } else if (annotation instanceof Query) {
                if(isDynamicUrl) {
                    throw new IllegalArgumentException("You can't use @Query with @DynamicURL.");
                }
                String encodedQuery = Utils.encodeQuery(arg, ((Query) annotation).encoded());
                parameterManager.addQueryParam(((Query) annotation).value(), encodedQuery);
            } else if (annotation instanceof Queries) {
                if(isDynamicUrl) {
                    throw new IllegalArgumentException("You can't use @Queries with @DynamicURL.");
                }
                List<Object> queries = Utils.checkIsList(arg);
                for (Object query : queries) {
                    String encodedQuery = Utils.encodeQuery(query, ((Queries) annotation).encoded());
                    parameterManager.addQueriesParam(((Queries) annotation).value(), encodedQuery);
                }
            } else if (annotation instanceof QueryMap) {
                if(isDynamicUrl) {
                    throw new IllegalArgumentException("You can't use @QueryMap with @DynamicURL.");
                }
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
                if (!isFormEncoded) {
                    throw new IllegalArgumentException("'@Field' needs a '@FormUrlEncoded' on your method.");
                }
                if (((Field) annotation).encoded()) {
                    formBuilder.addEncoded(((Field) annotation).value(), String.valueOf(arg));
                } else {
                    formBuilder.add(((Field) annotation).value(), String.valueOf(arg));
                }
            } else if (annotation instanceof FieldMap) {
                if (!isFormEncoded) {
                    throw new IllegalArgumentException("'@FieldMap' needs a '@FormUrlEncoded' on your method.");
                }
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
            } else if (annotation instanceof RequestBody) {
                if (!hasBody) {
                    throw new IllegalArgumentException(httpMethod + " method cannot have a request body.");
                }
                parameterManager.setRawRequestBody(arg);
            } else if (annotation instanceof URL) {
                if (!(arg instanceof String || arg instanceof java.net.URL
                        || arg instanceof URI || arg instanceof HttpUrl)) {
                    throw new IllegalArgumentException("The @URL value must be of type String, URL, URI, or HttpUrl.");
                }
                if (completedUrl != null) {
                    throw new IllegalArgumentException("You can use @URL once.");
                }
                completedUrl = Utils.getHttpUrl(arg);
            }
        }
    }
}
