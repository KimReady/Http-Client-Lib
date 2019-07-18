package com.naver.httpclientsdk;

import com.naver.httpclientlib.HttpClient;
import com.naver.httpclientsdk.mockInterface.ValidHttpService;

import org.junit.Test;

public class AsyncTest {
    HttpClient httpClient = new HttpClient.Builder("http://jsonplaceholder.typicode.com")
            .build();
    ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);

    @Test
    public void get_posts_success_by_async() {

    }

    @Test
    public void get_posts_fail_by_async() {

    }

    @Test
    public void get_response_to_specified_thread() {

    }
}
