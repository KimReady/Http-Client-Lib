package com.naver.httpclientsdk;

import com.naver.httpclientlib.CallTask;
import com.naver.httpclientlib.HttpClient;
import com.naver.httpclientlib.RequestFactory;
import com.naver.httpclientlib.annotation.QueryMap;

import org.junit.Test;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * unit test for HttpClient
 *
 */
public class HttpClientUnitTest {
    HttpClient httpClient = new HttpClient.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .build();
    ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);

    @Test
    public void get_posts() {
        CallTask<String> posts = validHttpService.getPosts();
        try {
            String result = posts.execute();
            System.out.println(result);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void get_posts_by_id() {
        CallTask<String> posts = validHttpService.getPostsById(5);
        try {
            String result = posts.execute();
            System.out.println(result);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void get_comments_by_id() {
        CallTask<String> comments = validHttpService.getCommentsById(3);
        try {
            String result = comments.execute();
            System.out.println(result);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void get_comments_by_postId() {
        CallTask<String> comments = validHttpService.getCommentsByPostId(2);
        try {
            String result = comments.execute();
            System.out.println(result);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void java_test() {
        System.out.println(ValidHttpService.class.getMethods()[0].getAnnotations()[0].annotationType().getName());
    }

}