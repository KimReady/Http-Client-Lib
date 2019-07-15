package com.naver.httpclientlib;

import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.ConnectionSpec;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import static com.naver.httpclientlib.Utils.checkNotNull;

public final class HttpClient {
    private final HttpUrl baseUrl;
    private final okhttp3.Call.Factory callFactory;

    public HttpClient(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.callFactory = builder.callFactory;
    }

    public <T> T create(Class<T> service) {
        if(!service.isInterface()) {
            throw new IllegalArgumentException("declarations must be interface.");
        }

        return (T) Proxy.newProxyInstance(service.getClassLoader()
                , new Class<?>[]{service}
                , new HttpInvocationHandler(this));
    }

    HttpClient(HttpUrl baseUrl, Call.Factory callFactory) {
        this.baseUrl = baseUrl;
        this.callFactory = callFactory;
    }

    HttpUrl getBaseUrl() {
        return baseUrl;
    }

    okhttp3.Call.Factory getCallFactory() {
        return callFactory;
    }

    /**
     * Builder
     */
    public static final class Builder {
        private HttpUrl baseUrl;
        private okhttp3.Call.Factory callFactory;

        public Builder() {

        }

        public Builder(HttpClient httpClient) {
            this.baseUrl = httpClient.baseUrl;
            this.callFactory = httpClient.callFactory;
        }

        public Builder baseUrl(String baseUrl) {
            checkNotNull(baseUrl, "URL is null");
            return baseUrl(HttpUrl.get(baseUrl));
        }

        public Builder baseUrl(URL baseUrl) {
            checkNotNull(baseUrl, "URL is null");
            return baseUrl(HttpUrl.get(baseUrl));
        }

        public Builder baseUrl(URI baseUrl) {
            checkNotNull(baseUrl, "URL is null");
            return baseUrl(HttpUrl.get(baseUrl));
        }

        public Builder baseUrl(HttpUrl baseUrl) {
            checkNotNull(baseUrl, "URL is null");
            this.baseUrl = baseUrl;
            return this;
        }

        public HttpClient build() {
            if (baseUrl == null) {
                throw new IllegalStateException("BaseURL is needed");
            }

            if (callFactory == null) {
                this.callFactory = new OkHttpClient.Builder()
                        .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT))
                        .build();
            }

            return new HttpClient(baseUrl, callFactory);
        }
    }
}
