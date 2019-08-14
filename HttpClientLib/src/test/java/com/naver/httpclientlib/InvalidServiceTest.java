package com.naver.httpclientlib;

import com.naver.httpclientlib.mock.Comment;
import com.naver.httpclientlib.mock.Post;
import com.naver.httpclientlib.mockInterface.InvalidHttpService;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Invalid unit test for HttpClient
 */
public class InvalidServiceTest {
    private InvalidHttpService invalidHttpService;

    @Before
    public void setUp() {
        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl("http://jsonplaceholder.typicode.com/")
                .build();
        invalidHttpService = httpClient.create(InvalidHttpService.class);
    }

    @Test(expected = RuntimeException.class)
    public void returnTypeNotCallTask() {
        try {
            List<Post> posts = invalidHttpService.getWithIllegalReturnType();
        } catch(RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void duplicatePathParameters() {
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
    public void morePathParametersInUrlThanActualParameters() {
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
    public void moreActualPathParametersThanUrlPathParameters() {
        try {
            CallTask<Post> post = invalidHttpService.getMoreActualPathParamThanUrl(5, "title test");
        } catch(RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test(expected = RuntimeException.class)
    public void getMethodIncludeRequestBody() {
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
    public void putWithoutRequestBody() {
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
    public void getPostsDuplicateUrl() {
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
    public void getPostsByNullOfDynamicUrl() {
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
    public void getPostsWithoutUrl() {
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
