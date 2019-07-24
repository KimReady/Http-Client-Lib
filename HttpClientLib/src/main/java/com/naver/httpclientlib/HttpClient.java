package com.naver.httpclientlib;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import static com.naver.httpclientlib.Utils.checkNotNull;

public final class HttpClient {
    private final HttpUrl baseUrl;
    private final okhttp3.Call.Factory callFactory;
    private final ExecutorService executorService;
    private final GsonBuilder gsonBuilder;

    public HttpClient(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.callFactory = builder.callFactory;
        this.executorService = builder.executorService;
        this.gsonBuilder = builder.gsonBuilder;
    }

    public <T> T create(Class<T> service) {
        if (!service.isInterface()) {
            throw new IllegalArgumentException("declarations must be interface.");
        }

        return (T) Proxy.newProxyInstance(service.getClassLoader()
                , new Class<?>[]{service}
                , new HttpInvocationHandler(this));
    }

    HttpUrl getBaseUrl() {
        return baseUrl;
    }

    okhttp3.Call.Factory getCallFactory() {
        return callFactory;
    }

    ExecutorService getExecutorService() {
        return executorService;
    }

    GsonBuilder gsonBuilder() {
        return gsonBuilder;
    }

    /**
     * Builder
     */
    public static final class Builder {
        private HttpUrl baseUrl;
        private okhttp3.Call.Factory callFactory;
        private GsonBuilder gsonBuilder;
        private ExecutorService executorService;
        private okhttp3.Interceptor applicationInterceptor;
        private okhttp3.Interceptor networkInterceptor;
        private Timeout callTimeout;
        private Timeout connectTimeout;
        private Timeout readTimeout;
        private Timeout writeTimeout;

        public Builder() {
            this.callTimeout = new Timeout(Utils.DEFAULT_CALL_TIMEOUT, TimeUnit.MILLISECONDS);
            this.connectTimeout = new Timeout(Utils.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
            this.readTimeout = new Timeout(Utils.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
            this.writeTimeout = new Timeout(Utils.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
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

        Builder baseUrl(HttpUrl baseUrl) {
            checkNotNull(baseUrl, "base URL is null");
            this.baseUrl = baseUrl;
            return this;
        }

        Builder callFactory(okhttp3.Call.Factory callFactory) {
            this.callFactory = callFactory;
            return this;
        }

        Builder gsonBuilder(GsonBuilder gsonBuilder) {
            this.gsonBuilder = gsonBuilder;
            return this;
        }

        public Builder callTimeout(long timeout, TimeUnit unit) {
            callTimeout.time = timeout;
            callTimeout.timeUnit = unit;
            return this;
        }

        public Builder connectTimeout(long timeout, TimeUnit unit) {
            connectTimeout.time = timeout;
            connectTimeout.timeUnit = unit;
            return this;
        }

        public Builder readTimeout(long timeout, TimeUnit unit) {
            readTimeout.time = timeout;
            readTimeout.timeUnit = unit;
            return this;
        }

        public Builder writeTimeout(long timeout, TimeUnit unit) {
            writeTimeout.time = timeout;
            writeTimeout.timeUnit = unit;
            return this;
        }

        public Builder executorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public Builder applicationInterceptor(final Interceptor interceptor) {
            this.applicationInterceptor = new okhttp3.Interceptor(){
                @Override
                public Response intercept(okhttp3.Interceptor.Chain chain) throws IOException {
                    InterceptorChain interceptorChain = new InterceptorChain(chain);
                    return interceptor.intercept(interceptorChain).getRawResponse();
                }
            };
            return this;
        }

        public Builder networkInterceptor(final Interceptor interceptor) {
            this.networkInterceptor = new okhttp3.Interceptor(){
                @Override
                public Response intercept(okhttp3.Interceptor.Chain chain) throws IOException {
                    InterceptorChain interceptorChain = new InterceptorChain(chain);
                    return interceptor.intercept(interceptorChain).getRawResponse();
                }
            };
            return this;
        }

        public HttpClient build() {
            if(gsonBuilder == null) {
                gsonBuilder = new GsonBuilder();
            }

            if (callFactory == null) {
                // TLS -> CLEARTEXT 순으로 연결 시도하도록 설정
                OkHttpClient.Builder callBuilder = new OkHttpClient.Builder()
                        .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT))
                        .callTimeout(callTimeout.time, callTimeout.timeUnit)
                        .connectTimeout(connectTimeout.time, connectTimeout.timeUnit)
                        .readTimeout(readTimeout.time, readTimeout.timeUnit)
                        .writeTimeout(writeTimeout.time, writeTimeout.timeUnit);

                if (applicationInterceptor != null) {
                    callBuilder.addInterceptor(applicationInterceptor);
                }
                if (networkInterceptor != null) {
                    callBuilder.addNetworkInterceptor(networkInterceptor);
                }

                this.callFactory = callBuilder.build();
            }

            if (executorService == null) {
                this.executorService = Executors.newCachedThreadPool();
            }

            return new HttpClient(this);
        }

        private class Timeout {
            long time;
            TimeUnit timeUnit;

            Timeout(long time, TimeUnit unit) {
                this.time = time;
                this.timeUnit = unit;
            }
        }
    }
}
