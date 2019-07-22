package com.naver.httpclientlib;

import com.naver.httpclientlib.converter.Converter;

import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.Dispatcher;
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
        private HttpUrl baseUrl;
        private okhttp3.Call.Factory callFactory;
        private Converter converter;
        private ExecutorService executorService;
        private Timeout callTimeout;
        private Timeout connectTimeout;
        private Timeout readTimeout;
        private Timeout writeTimeout;

        public Builder() {
            this.callTimeout = new Timeout(0, TimeUnit.MILLISECONDS);
            this.connectTimeout = new Timeout(10000, TimeUnit.MILLISECONDS);
            this.readTimeout = new Timeout(10000, TimeUnit.MILLISECONDS);
            this.writeTimeout = new Timeout(10000, TimeUnit.MILLISECONDS);
        }

        public Builder baseUrl(String baseUrl) {
            return baseUrl(HttpUrl.get(baseUrl));
        }

        public Builder baseUrl(URL baseUrl) {
            return baseUrl(HttpUrl.get(baseUrl));
        }

        public Builder baseUrl(URI baseUrl) {
            return baseUrl(HttpUrl.get(baseUrl));
        }

        public Builder baseUrl(HttpUrl baseUrl) {
            checkNotNull(baseUrl, "base URL is null");
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder converter(Converter converter) {
            this.converter = converter;
            return this;
        }

        public Builder callFactory(okhttp3.Call.Factory callFactory) {
            this.callFactory = callFactory;
            return this;
        }

        public Builder callTimeout(long timeout, TimeUnit unit) {
            callTimeout.timeout = timeout;
            callTimeout.timeUnit = unit;
            return this;
        }

        public Builder connectTimeout(long timeout, TimeUnit unit) {
            connectTimeout.timeout = timeout;
            connectTimeout.timeUnit = unit;
            return this;
        }

        public Builder readTimeout(long timeout, TimeUnit unit) {
            readTimeout.timeout = timeout;
            readTimeout.timeUnit = unit;
            return this;
        }

        public Builder writeTimeout(long timeout, TimeUnit unit) {
            writeTimeout.timeout = timeout;
            writeTimeout.timeUnit = unit;
            return this;
        }

        public Builder executorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public HttpClient build() {
            if (callFactory == null) {
                // TLS -> CLEARTEXT 순으로 연결 시도하도록 설정
                Dispatcher dispatcher = (executorService != null) ? new Dispatcher(executorService) : new Dispatcher();
                this.callFactory = new OkHttpClient.Builder()
                        .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT))
                        .dispatcher(dispatcher)
                        .callTimeout(callTimeout.timeout, callTimeout.timeUnit)
                        .connectTimeout(connectTimeout.timeout, connectTimeout.timeUnit)
                        .readTimeout(readTimeout.timeout, readTimeout.timeUnit)
                        .writeTimeout(writeTimeout.timeout, writeTimeout.timeUnit)
                        .build();
            }

            return new HttpClient(this);
        }

        private class Timeout {
            long timeout;
            TimeUnit timeUnit;

            Timeout(long timeout, TimeUnit unit) {
                this.timeout = timeout;
                this.timeUnit = unit;
            }
        }
    }
}
