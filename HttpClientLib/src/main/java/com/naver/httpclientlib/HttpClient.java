package com.naver.httpclientlib;

import com.naver.httpclientlib.converter.Converter;

import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import static com.naver.httpclientlib.Utils.checkNotNull;

public final class HttpClient {
    private final HttpUrl baseUrl;
    private final okhttp3.Call.Factory callFactory;
    private Converter converter;

    public HttpClient(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.callFactory = builder.callFactory;
        this.converter = builder.converter;
    }

    public <T> T create(Class<T> service) {
        if(!service.isInterface()) {
            throw new IllegalArgumentException("declarations must be interface.");
        }

        return (T) Proxy.newProxyInstance(service.getClassLoader()
                , new Class<?>[]{service}
                , new HttpInvocationHandler(this));
    }

    public void setConverter(Converter converter) {
        this.converter = converter;
    }

    HttpUrl getBaseUrl() {
        return baseUrl;
    }

    okhttp3.Call.Factory getCallFactory() {
        return callFactory;
    }

    Converter getConverter() {
        return converter;
    }

    /**
     * Builder
     */
    public static final class Builder {
        private final HttpUrl baseUrl;
        private okhttp3.Call.Factory callFactory;
        private Converter converter;
        private long timeout;

        public Builder(String baseUrl) {
            this(HttpUrl.get(baseUrl));
        }

        public Builder(URL baseUrl) {
            this(HttpUrl.get(baseUrl));
        }

        public Builder(URI baseUrl) {
            this(HttpUrl.get(baseUrl));
        }

        public Builder(HttpUrl baseUrl) {
            checkNotNull(baseUrl, "URL is null");
            this.baseUrl = baseUrl;
            this.timeout = 0;
        }

        public Builder converter(Converter converter) {
            this.converter = converter;
            return this;
        }

        public Builder callFactory(okhttp3.Call.Factory callFactory) {
            this.callFactory = callFactory;
            return this;
        }

        public Builder timeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public HttpClient build() {
            if (baseUrl == null) {
                throw new IllegalStateException("BaseURL is needed");
            }

            if (callFactory == null) {
                // TLS -> CLEARTEXT 순으로 연결 시도
                this.callFactory = new OkHttpClient.Builder()
                        .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT))
                        .callTimeout(timeout, TimeUnit.MILLISECONDS)
                        .build();
            }

            return new HttpClient(this);
        }
    }
}
