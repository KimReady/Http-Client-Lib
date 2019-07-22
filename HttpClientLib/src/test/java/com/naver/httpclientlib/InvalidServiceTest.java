package com.naver.httpclientlib;

import com.naver.httpclientlib.mock.Comment;
import com.naver.httpclientlib.mock.Post;
import com.naver.httpclientlib.mockInterface.InvalidHttpService;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Invalid unit test for HttpClient
 */
public class InvalidServiceTest {
    HttpClient httpClient = new HttpClient.Builder().baseUrl("http://jsonplaceholder.typicode.com")
            .build();
    InvalidHttpService invalidHttpService = httpClient.create(InvalidHttpService.class);

    @Test(expected = RuntimeException.class)
    public void return_type_not_CallTask() {
        try {
            List<Post> posts = invalidHttpService.getWithIllegalReturnType();
        } catch(RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void duplicate_path_parameters() {
        try {
            CallTask<Post> post = invalidHttpService.getDuplicatePathParam(5, 10);
            Post result = post.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void more_path_parameters_in_url_than_actual_parameters() {
        try {
            CallTask<Post> post = invalidHttpService.getMorePathParamThanActualParam();
            Post result = post.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void more_actual_path_parameters_than_url_path_parameters() {
        try {
            CallTask<Post> post = invalidHttpService.getMoreActualPathParamThanUrl(5, "title test");
        } catch(RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void get_method_include_request_body() {
        try {
            CallTask<List<Comment>> comments = invalidHttpService.getMethodIncludeRequestBody(3, "body");
            List<Comment> result = comments.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void put_without_request_body() {
        try {
            CallTask<Post> call = invalidHttpService.putPostsWithoutRequestBody(5);
            Response<Post> response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void get_posts_duplicate_url() {
        try {
            CallTask<List<Post>> call = invalidHttpService.getPostsDuplicateURL("http://jsonplaceholder.typicode.com/posts");
            List<Post> posts = call.execute().body();
        } catch(IOException e) {
            e.printStackTrace();
        } catch(RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void get_posts_by_null_of_dynamic_url() {
        try {
            CallTask<List<Post>> call = invalidHttpService.getPostsByNullOfDynamicURL(null);
            List<Post> posts = call.execute().body();
        } catch(IOException e) {
            e.printStackTrace();
        } catch(RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void get_posts_without_url() {
        try {
            CallTask<List<Post>> call = invalidHttpService.getPostsWithoutURL();
            List<Post> posts = call.execute().body();
        } catch(IOException e) {
            e.printStackTrace();
        } catch(RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

}
