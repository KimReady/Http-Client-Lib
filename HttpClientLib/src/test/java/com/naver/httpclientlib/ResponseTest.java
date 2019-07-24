package com.naver.httpclientlib;

import com.naver.httpclientlib.mock.Post;
import com.naver.httpclientlib.mockInterface.ValidHttpService;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class ResponseTest {
    HttpClient httpClient = new HttpClient.Builder().baseUrl("http://jsonplaceholder.typicode.com/")
            .build();
    ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);

    @Test
    public void responseHeaderByName() {
        String contentType = "application/json; charset=utf-8";
        CallTask<List<Post>> call = validHttpService.getPosts();
        try {
            Response<List<Post>> res = call.execute();
            assertEquals(contentType, res.header("content-type"));
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void responseHeaderByNameWithDefaultValue() {
        String defaultHeader = "no type";
        CallTask<List<Post>> call = validHttpService.getPosts();
        try {
            Response<List<Post>> res = call.execute();
            assertEquals(defaultHeader, res.header("content-types", defaultHeader));
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void responseHeaders() {
        CallTask<List<Post>> call = validHttpService.getPosts();
        try {
            Response<List<Post>> res = call.execute();
            List<String> headers = res.headers("Vary");
            assertNotNull(headers);
            for(String header : headers) {
                System.out.println(header);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void responseBody() {
        CallTask<List<Post>> call = validHttpService.getPosts();
        try {
            Response<List<Post>> res = call.execute();
            List<Post> posts = res.body();
            assertNotNull(posts);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void responseCode() {
        CallTask<List<Post>> call = validHttpService.getPosts();
        try {
            Response<List<Post>> res = call.execute();
            assertEquals(200, res.code());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void responseIsSuccessful() {
        CallTask<List<Post>> call = validHttpService.getPosts();
        try {
            Response<List<Post>> res = call.execute();
            assertTrue(res.isSuccessful());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void responseIsRedirect() {
        try {
            CallTask<List<Post>> call = validHttpService.getPostsByDynamicURL("http://www.publicobject.com/helloworld.txt");
            Response res = call.execute();
            assertEquals(200, res.code());
            assertFalse(res.isRedirect());
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}