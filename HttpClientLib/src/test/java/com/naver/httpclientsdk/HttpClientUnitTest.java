package com.naver.httpclientsdk;

import com.naver.httpclientlib.CallTask;
import com.naver.httpclientlib.HttpClient;
import com.naver.httpclientlib.Response;
import com.naver.httpclientsdk.TestModel.Comment;
import com.naver.httpclientsdk.TestModel.Post;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * unit test for HttpClient
 */
public class HttpClientUnitTest {
    HttpClient httpClient = new HttpClient.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .build();
    ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);
    InvalidHttpService invalidHttpService = httpClient.create(InvalidHttpService.class);

    @Test
    public void get_posts() {
        CallTask<List<Post>> posts = validHttpService.getPosts();
        try {
            Response<List<Post>> res = posts.execute();
            System.out.println(res.header("content-type"));
            List<Post> result = res.body();
            for (Post post : result) {
                System.out.println(post.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void get_posts_by_id() {
        CallTask<Post> post = validHttpService.getPostsById(5);
        try {
            Post result = post.execute().body();
            System.out.println(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void get_comments_by_id() {
        CallTask<List<Comment>> comments = validHttpService.getCommentsById(3);
        try {
            List<Comment> result = comments.execute().body();
            for (Comment comment : result) {
                System.out.println(comment.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void get_comments_by_postId() {
        CallTask<List<Comment>> comments = validHttpService.getCommentsByPostId(2);
        try {
            List<Comment> result = comments.execute().body();
            for (Comment comment : result) {
                System.out.println(comment.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void java_test() {
        try {
            Method method = validHttpService.getClass().getMethod("getPosts");
            Type type = method.getGenericReturnType();
            System.out.println(type);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

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