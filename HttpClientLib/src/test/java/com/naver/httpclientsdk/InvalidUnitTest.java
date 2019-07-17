package com.naver.httpclientsdk;

import com.naver.httpclientlib.HttpClient;
import com.naver.httpclientsdk.TestModel.Post;

import org.junit.Assert;
import org.junit.Test;

/**
 * Invalid unit test for HttpClient
 */
public class InvalidUnitTest {
    HttpClient httpClient = new HttpClient.Builder("https://jsonplaceholder.typicode.com")
            .build();
    InvalidHttpService invalidHttpService = httpClient.create(InvalidHttpService.class);

    @Test
    public void illegal_return_type() {
        try {
            Post post = invalidHttpService.getPosts_return_illagal();
            System.out.println(post.toString());
            Assert.fail();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
