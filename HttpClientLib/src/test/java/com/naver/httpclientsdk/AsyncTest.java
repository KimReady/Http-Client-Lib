package com.naver.httpclientsdk;

import com.naver.httpclientlib.CallBack;
import com.naver.httpclientlib.CallTask;
import com.naver.httpclientlib.HttpClient;
import com.naver.httpclientlib.Response;
import com.naver.httpclientsdk.mock.Post;
import com.naver.httpclientsdk.mockInterface.ValidHttpService;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AsyncTest {
    HttpClient httpClient = new HttpClient.Builder().baseUrl("http://jsonplaceholder.typicode.com")
            .build();
    ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);

    @Test
    public void get_posts_success_by_async() {
        final CountDownLatch latch = new CountDownLatch(1);
        CallTask<List<Post>> posts = validHttpService.getPosts();
        posts.enqueue(new CallBack() {
            @Override
            public void onResponse(Response<?> response) {
                System.out.println("Response!");
                latch.countDown();
            }

            @Override
            public void onFailure(Response<?> response, IOException e) {
                System.out.println(e.getMessage());
                latch.countDown();
            }
        });
        try {
            Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
        } catch(InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void get_posts_failure_by_cancel() {
        final CountDownLatch latch = new CountDownLatch(1);
        CallTask<List<Post>> posts = validHttpService.getPosts();
        CallBack callback = new CallBack() {
            @Override
            public void onResponse(Response<?> response) {
                System.out.println("Response!");
                latch.countDown();
            }

            @Override
            public void onFailure(Response<?> response, IOException e) {
                System.out.println(e.getMessage());
                latch.countDown();
            }
        };
        posts.enqueue(callback);
        try {
            posts.cancel();
            Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
            Assert.assertTrue(posts.isCanceled());
        } catch(InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void get_posts_fail_by_async() {
        Class<?> clazz = int[].class;
        System.out.println(clazz instanceof Class<?>);
    }

    @Test
    public void get_response_to_specified_thread() {

    }
}
