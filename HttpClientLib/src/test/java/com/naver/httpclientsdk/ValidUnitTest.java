package com.naver.httpclientsdk;

import com.naver.httpclientlib.CallTask;
import com.naver.httpclientlib.HttpClient;
import com.naver.httpclientlib.Response;
import com.naver.httpclientsdk.TestModel.Comment;
import com.naver.httpclientsdk.TestModel.Post;
import com.naver.httpclientsdk.TestModel.User;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * valid unit test for HttpClient
 */
public class ValidUnitTest {
    HttpClient httpClient = new HttpClient.Builder()
            .baseUrl("http://jsonplaceholder.typicode.com")
            .build();
    ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);

    @Test
    public void get_posts() {
        CallTask<List<Post>> posts = validHttpService.getPosts();
        try {
            Response<List<Post>> res = posts.execute();
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
    public void get_posts_by_id_using_path_param() {
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
    public void get_comments_by_id_using_path_param() {
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
    public void get_comments_by_postId_using_query() {
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
    public void get_posts_by_user_id_using_query_map() {
        Map<String, Integer> query = new HashMap<>();
        query.put("userId", 3);
        CallTask<List<Post>> posts = validHttpService.getPostsByUserId(query);
        try {
            Response<List<Post>> res = posts.execute();
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
    public void get_users_using_composite_object() {
        CallTask<List<User>> users = validHttpService.getUsers();
        try {
            Response<List<User>> res = users.execute();
            List<User> result = res.body();
            for (User user : result) {
                System.out.println(user.toString());
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

}