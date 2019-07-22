package com.naver.httpclientlib;

import com.naver.httpclientlib.mockInterface.ValidHttpService;
import com.naver.httpclientlib.mock.Comment;
import com.naver.httpclientlib.mock.Post;
import com.naver.httpclientlib.mock.SkipPost;
import com.naver.httpclientlib.mock.User;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * valid unit test for HttpClient
 */
public class ValidServiceTest {
    HttpClient httpClient = new HttpClient.Builder().baseUrl("http://jsonplaceholder.typicode.com")
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
        } catch (IOException e) {
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
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void get_posts_skip_title_by_id_using_path_param() {
        CallTask<SkipPost> skippost = validHttpService.getPostsSkipTitleById(5);
        try {
            SkipPost result = skippost.execute().body();
            Assert.assertTrue(result.getId() == 5);
        } catch (IOException e) {
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
                System.out.println(comment);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void get_comments_by_postId_using_query() {
        List<Integer> postIdList = new ArrayList<>();
        postIdList.add(3);
        postIdList.add(4);
        CallTask<List<Comment>> comments = validHttpService.getCommentsByPostId(postIdList);
        try {
            List<Comment> result = comments.execute().body();
            for (Comment comment : result) {
                Assert.assertTrue(comment.getPostId() == 3 || comment.getPostId() == 4);
            }
        } catch (IOException e) {
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
                Assert.assertTrue(post.getUserId() == 3);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void get_method_with_header() {
        CallTask<List<Post>> posts = validHttpService.getPostsWithHeader("text/html");
        try {
            Response<List<Post>> res = posts.execute();
            System.out.println(res.header("content-type"));
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void get_method_with_headers() {
        CallTask<List<Post>> posts = validHttpService.getPostsWithHeaders();
        try {
            Response<List<Post>> res = posts.execute();
            System.out.println(res.header("content-type"));
        } catch (IOException e) {
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
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void delete_posts_by_id() {
        CallTask<Post> post = validHttpService.deletePostById(5);
        try {
            Response<Post> p = post.execute();
            System.out.println(p.header("content-type"));
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void put_posts_by_id() {
        String newTitle = "new sample title";
        Post newPost = new Post(4, 5, newTitle, "new sample body", 300);
        CallTask<Post> call = validHttpService.putPostsById(5, newPost);
        try {
            Response<Post> response = call.execute();
            Post post = response.body();
            Assert.assertEquals(newTitle, post.getTitle());
        } catch (IOException e) {
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
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void post_posts_by_formUrlEncoded() {
        String newTitle = "new sample title";
        CallTask<Post> post = validHttpService.postPostsFormUrlEncoded(111, newTitle);
        try {
            Post result = post.execute().body();
            Assert.assertEquals(newTitle, result.getTitle());
        } catch (IOException e) {
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
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void get_posts_using_dynamic_url() {
        try {
            CallTask<List<Post>> call = validHttpService.getPostsByDynamicURL("http://jsonplaceholder.typicode.com/posts");
            List<Post> posts = call.execute().body();
            for (Post post : posts) {
                System.out.println(post);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void no_url_http_client_but_dynamic_url() {
        try {
            HttpClient client = new HttpClient.Builder().build();
            ValidHttpService noUrlService = client.create(ValidHttpService.class);
            CallTask<List<Post>> call = noUrlService.getPostsByDynamicURL("http://jsonplaceholder.typicode.com/posts?id=3");
            List<Post> posts = call.execute().body();
            for (Post post : posts) {
                System.out.println(post);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void get_posts_using_dynamic_url_with_query() {
        try {
            CallTask<List<Post>> call = validHttpService.getPostsByDynamicURLWithQuery("http://jsonplaceholder.typicode.com/posts?pw=1", 3);
            List<Post> posts = call.execute().body();
            for (Post post : posts) {
                System.out.println(post);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void mass_call() {
        for (int i = 0; i < 10; i++) {
            CallTask<List<Post>> posts = validHttpService.getPosts();
            try {
                posts.execute();
            } catch (IOException e) {
                e.printStackTrace();
                Assert.fail();
            }
        }
    }

    @Test
    public void set_read_timeout() {
        try {
            HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com")
                .readTimeout(1, TimeUnit.MILLISECONDS)
                .build();
        ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);

        CallTask<List<Post>> posts = validHttpService.getPosts();
            Response<List<Post>> res = posts.execute();
            res.body();
            Assert.fail();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void set_connect_timeout() {
        try {
            HttpClient httpClient = new HttpClient.Builder()
                    .baseUrl("http://jsonplaceholder.typicode.com")
                    .connectTimeout(1, TimeUnit.MILLISECONDS)
                    .build();
            ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);

            CallTask<List<Post>> posts = validHttpService.getPosts();
            Response<List<Post>> res = posts.execute();
            res.body();
            Assert.fail();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void set_call_timeout() {
        try {
            HttpClient httpClient = new HttpClient.Builder()
                    .baseUrl("http://jsonplaceholder.typicode.com")
                    .callTimeout(1, TimeUnit.MILLISECONDS)
                    .build();
            ValidHttpService validHttpService = httpClient.create(ValidHttpService.class);

            CallTask<List<Post>> posts = validHttpService.getPosts();
            Response<List<Post>> res = posts.execute();
            res.body();
            Assert.fail();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void call_two_method_at_once() {
        CallTask<List<Comment>> call1 = validHttpService.getCommentsById(3);
        try {
            List<Comment> comment1 = call1.execute().body();
            System.out.println(comment1.get(0));
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

        List<Integer> postIdList = new ArrayList<>();
        postIdList.add(3);
        postIdList.add(4);
        CallTask<List<Comment>> call2 = validHttpService.getCommentsByPostId(postIdList);
        try {
            List<Comment> comment2 = call2.execute().body();
            for (Comment comment : comment2) {
                Assert.assertTrue(comment.getPostId() == 3 || comment.getPostId() == 4);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}