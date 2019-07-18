package com.naver.httpclientsdk;

import com.naver.httpclientlib.CallTask;
import com.naver.httpclientlib.HttpClient;
import com.naver.httpclientlib.Response;
import com.naver.httpclientsdk.mockInterface.InvalidHttpService;
import com.naver.httpclientsdk.mock.Comment;
import com.naver.httpclientsdk.mock.Post;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Invalid unit test for HttpClient
 */
public class InvalidServiceTest {
    HttpClient httpClient = new HttpClient.Builder("http://jsonplaceholder.typicode.com")
            .build();
    InvalidHttpService invalidHttpService = httpClient.create(InvalidHttpService.class);

    @Test(expected = RuntimeException.class)
    public void return_type_not_CallTask() {
        List<Post> posts = invalidHttpService.getWithIllegalReturnType();
        for (Post post : posts) {
            System.out.println(post.toString());
        }
    }

    @Test(expected = RuntimeException.class)
    public void duplicate_path_parameters() {
        try {
            CallTask<Post> post = invalidHttpService.getDuplicatePathParam(5, 10);
            Post result = post.execute().body();
            System.out.println(result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = RuntimeException.class)
    public void more_path_parameters_in_url_than_actual_parameters() {
        try {
            CallTask<Post> post = invalidHttpService.getMorePathParamThanActualParam();
            Post result = post.execute().body();
            System.out.println(result.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = RuntimeException.class)
    public void more_actual_path_parameters_than_url_path_parameters() {
        CallTask<Post> post = invalidHttpService.getMoreActualPathParamThanUrl(5, "title test");
    }

    @Test(expected = RuntimeException.class)
    public void get_method_include_request_body() {
        try {
            CallTask<List<Comment>> comments = invalidHttpService.getMethodIncludeRequestBody(3, "body");
            List<Comment> result = comments.execute().body();
            for (Comment comment : result) {
                System.out.println(comment.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = RuntimeException.class)
    public void put_without_request_body() {
        try {
            CallTask<Post> call = invalidHttpService.putPostsWithoutRequestBody(5);
            Response<Post> response = call.execute();
            System.out.println(response.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
