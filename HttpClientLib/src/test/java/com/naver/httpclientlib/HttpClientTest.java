package com.naver.httpclientlib;

import com.google.gson.GsonBuilder;

import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

import static org.junit.Assert.*;

public class HttpClientTest {

    @Test
    public void createWithStringUrl() {
        String baseUrl = "http://jsonplaceholder.typicode.com/";
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl(baseUrl)
                .build();
        assertEquals(baseUrl, httpClient.getBaseUrl().toString());
    }

    @Test
    public void createWithURL() throws MalformedURLException {
        String baseUrl = "http://jsonplaceholder.typicode.com/";
        URL url = new URL(baseUrl);
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl(url)
                .build();
        assertEquals(url.toString(), httpClient.getBaseUrl().toString());
    }

    @Test
    public void createWithURI() throws URISyntaxException {
        String baseUrl = "http://jsonplaceholder.typicode.com/";
        URI uri = new URI(baseUrl);
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl(uri)
                .build();
        assertEquals(uri.toString(), httpClient.getBaseUrl().toString());
    }

    @Test
    public void createWithoutBaseUrl() {
        HttpClient httpClient = new HttpClient.Builder()
                .build();
        assertNull(httpClient.getBaseUrl());
    }

    @Test
    public void createWithCallFactory() {
        String baseUrl = "http://jsonplaceholder.typicode.com/";
        Call.Factory callFactory = new OkHttpClient();
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl(baseUrl)
                .callFactory(callFactory)
                .build();
        assertEquals(callFactory, httpClient.getCallFactory());
    }

    @Test
    public void createWithExecutorService() {
        String baseUrl = "http://jsonplaceholder.typicode.com/";
        ExecutorService executorService = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
        );
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl(baseUrl)
                .executorService(executorService)
                .build();
        assertEquals(executorService, httpClient.getExecutorService());
    }

    @Test
    public void createWithCallTimeout() {
        long timeout = 100;
        String baseUrl = "http://jsonplaceholder.typicode.com/";
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl(baseUrl)
                .callTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();
        assertEquals(timeout, ((OkHttpClient) httpClient.getCallFactory()).callTimeoutMillis());
    }

    @Test
    public void createWithConnectTimeout() {
        long timeout = 100;
        String baseUrl = "http://jsonplaceholder.typicode.com/";
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl(baseUrl)
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();
        assertEquals(timeout, ((OkHttpClient) httpClient.getCallFactory()).connectTimeoutMillis());
    }

    @Test
    public void createWithReadTimeout() {
        long timeout = 100;
        String baseUrl = "http://jsonplaceholder.typicode.com/";
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl(baseUrl)
                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();
        assertEquals(timeout, ((OkHttpClient) httpClient.getCallFactory()).readTimeoutMillis());
    }

    @Test
    public void createWithWriteTimeout() {
        long timeout = 100;
        String baseUrl = "http://jsonplaceholder.typicode.com/";
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl(baseUrl)
                .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();
        assertEquals(timeout, ((OkHttpClient) httpClient.getCallFactory()).writeTimeoutMillis());
    }

    @Test
    public void createWithApplicationInterceptor() {
        String baseUrl = "http://jsonplaceholder.typicode.com/";
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                return chain.proceed();
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl(baseUrl)
                .applicationInterceptor(interceptor)
                .build();
        assertNotNull(((OkHttpClient) httpClient.getCallFactory()).interceptors().get(0));
    }

    @Test
    public void createWithNetworkInterceptor() {
        String baseUrl = "http://jsonplaceholder.typicode.com/";
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(InterceptorChain chain) throws IOException {
                return chain.proceed();
            }
        };
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl(baseUrl)
                .networkInterceptor(interceptor)
                .build();
        assertNotNull(((OkHttpClient) httpClient.getCallFactory()).networkInterceptors().get(0));
    }

    @Test
    public void createWithGsonBuilder() {
        String baseUrl = "http://jsonplaceholder.typicode.com/";
        GsonBuilder gsonBuilder = new GsonBuilder()
                .setLenient()
                .disableHtmlEscaping()
                .serializeNulls();
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl(baseUrl)
                .gsonBuilder(gsonBuilder)
                .build();
        assertEquals(gsonBuilder, httpClient.gsonBuilder());
    }
}