package com.naver.httpclientsdk;

import com.naver.httpclientlib.CallTask;
import com.naver.httpclientlib.HttpClient;
import com.naver.httpclientlib.Response;
import com.naver.httpclientsdk.TestModel.Comment;
import com.naver.httpclientsdk.TestModel.Post;
import com.naver.httpclientsdk.TestModel.User;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * valid unit test for HttpClient
 */
public class ValidUnitTest {
    HttpClient httpClient = new HttpClient.Builder("http://jsonplaceholder.typicode.com")
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
        Integer[] postIds = new Integer[] {1, 3};
        List<Integer> postIdList = new ArrayList<>();
        postIdList.add(3);
        postIdList.add(4);
        CallTask<List<Comment>> comments = validHttpService.getCommentsByPostId(postIdList);
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
    public void delete_posts_by_id() {
        CallTask<Post> post = validHttpService.deletePostById(5);
        try {
            post.execute();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void get_posts_head_method() {
        CallTask<Void> posts = validHttpService.getPostsForHeadMethod();
        try {
            Response<Void> res = posts.execute();
            System.out.println(res.header("content-type"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void post_posts_by_formUrlEncoded() {
        CallTask<Post> post = validHttpService.postPostsFormUrlEncoded(111, "new title");
        try {
            Post result = post.execute().body();
            System.out.println(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void post_posts_by_request_body() {
        Post newPost = new Post(111, 111, "abc", "defg", 200);
        CallTask<Post> post = validHttpService.postPosts(newPost);
        try {
            Post result = post.execute().body();
            System.out.println(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void java_test() {
        try {
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

}