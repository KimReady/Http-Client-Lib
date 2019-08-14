package com.naver.httpclienttest;

import com.naver.httpclientlib.CallTask;
import com.naver.httpclientlib.RequestMethod;
import com.naver.httpclientlib.annotation.DynamicURL;
import com.naver.httpclientlib.annotation.HeaderMap;
import com.naver.httpclientlib.annotation.PathParam;
import com.naver.httpclientlib.annotation.QueryMap;
import com.naver.httpclientlib.annotation.RequestBody;
import com.naver.httpclientlib.annotation.RequestMapping;
import com.naver.httpclientlib.annotation.URL;
import com.naver.httpclienttest.data.Post;

import java.util.List;
import java.util.Map;

public interface HttpService {
    @RequestMapping(value="/posts", method= RequestMethod.GET)
    CallTask<List<Post>> getPostsByQuery(@HeaderMap Map<String, String> headerMap, @QueryMap Map<String, String> queryMap);

    @RequestMapping(value="/posts/{id}", method= RequestMethod.GET)
    CallTask<Post> getPostsByPathParam(@HeaderMap Map<String, String> headerMap, @PathParam("id") Integer id);

    @RequestMapping(value="/posts", method= RequestMethod.POST)
    CallTask<Post> postPosts(@HeaderMap Map<String, String> headerMap, @RequestBody Post post);

    @RequestMapping(value="/posts/{id}", method= RequestMethod.PUT)
    CallTask<Post> putPostsById(@HeaderMap Map<String, String> headerMap, @PathParam("id") Integer id, @RequestBody Post newPost);

    @RequestMapping(value="/posts/{id}", method= RequestMethod.DELETE)
    CallTask<Post> deletePostById(@HeaderMap Map<String, String> headerMap, @PathParam("id") Integer id);

    @RequestMapping(value="/posts", method= RequestMethod.HEAD)
    CallTask<Void> getPostsForHeadMethod(@HeaderMap Map<String, String> headerMap);

    @DynamicURL(method= RequestMethod.GET)
    CallTask<List<Post>> getPostsByDynamicURLWithQuery(@HeaderMap Map<String, String> headerMap, @URL String url, @QueryMap Map<String, String> queryMap);

    @DynamicURL(method= RequestMethod.POST)
    CallTask<Post> postPostsByDynamicURL(@HeaderMap Map<String, String> headerMap, @URL String url, @RequestBody Post post);

    @DynamicURL(method= RequestMethod.PUT)
    CallTask<Post> putPostsByDynamicURL(@HeaderMap Map<String, String> headerMap, @URL String url, @RequestBody Post newPost);

    @DynamicURL(method= RequestMethod.DELETE)
    CallTask<Post> deletePostByDynamicURL(@HeaderMap Map<String, String> headerMap, @URL String url);

    @DynamicURL(method= RequestMethod.HEAD)
    CallTask<Void> getPostsForHeadMethodByDynamicURL(@HeaderMap Map<String, String> headerMap, @URL String url);

}
